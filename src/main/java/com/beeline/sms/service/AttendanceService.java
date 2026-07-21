package com.beeline.sms.service;

import com.beeline.sms.dto.AttendanceMarkRequest;
import com.beeline.sms.dto.AttendanceResponse;
import com.beeline.sms.dto.FingerprintAttendanceMarkRequest;
import com.beeline.sms.dto.QrAttendanceMarkRequest;
import com.beeline.sms.entity.Attendance;
import com.beeline.sms.entity.Staff;
import com.beeline.sms.entity.Student;
import com.beeline.sms.entity.User;
import com.beeline.sms.enums.Role;
import com.beeline.sms.repository.AttendanceRepository;
import com.beeline.sms.repository.StaffRepository;
import com.beeline.sms.repository.StudentRepository;
import com.beeline.sms.repository.UserRepository;
import com.machinezoo.sourceafis.FingerprintTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final FingerprintService fingerprintService;

    @Transactional
    public AttendanceResponse markAttendanceByStudentId(AttendanceMarkRequest request, Long markedByUserId) {
        User markedBy = validateMarkerPermission(markedByUserId);

        Student student = studentRepository.findByBranchIdAndStudentIdNo(request.getBranchId(), request.getStudentIdNo())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + request.getStudentIdNo()));

        attendanceRepository.findByStudentIdAndDate(student.getId(), request.getDate())
                .ifPresent(existing -> {
                    throw new RuntimeException("Attendance already marked for this student on " + request.getDate());
                });

        Attendance attendance = Attendance.builder()
                .student(student)
                .date(request.getDate())
                .present(request.getPresent() != null ? request.getPresent() : true)
                .markedBy(markedBy)
                .markedAt(LocalDateTime.now())
                .build();
        attendance = attendanceRepository.save(attendance);

        return toResponse(attendance);
    }

    /**
     * QR-based attendance marking. Identifies the student via the composite
     * (branchId, studentIdNo) pair decoded from the QR sticker — studentIdNo
     * alone is only unique per-branch, so branchId is required to disambiguate.
     * Unlike markAttendanceByStudentId, this UPSERTS rather than rejecting an
     * existing record: a student scanning twice (or a staff member re-scanning
     * a sticker still in camera frame) should just reconfirm attendance, not error.
     */
    @Transactional
    public AttendanceResponse markAttendanceByQr(QrAttendanceMarkRequest request, Long markedByUserId) {
        User markedBy = validateMarkerPermission(markedByUserId);

        Student student = studentRepository.findByBranchIdAndStudentIdNo(request.getBranchId(), request.getStudentIdNo())
                .orElseThrow(() -> new RuntimeException(
                        "No student found with ID " + request.getStudentIdNo() + " in the scanned branch"));

        Attendance attendance = attendanceRepository.findByStudentIdAndDate(student.getId(), request.getDate())
                .orElseGet(() -> Attendance.builder()
                        .student(student)
                        .date(request.getDate())
                        .build());

        attendance.setPresent(request.getPresent() != null ? request.getPresent() : true);
        attendance.setMarkedBy(markedBy);
        attendance.setMarkedAt(LocalDateTime.now());
        attendance = attendanceRepository.save(attendance);

        return toResponse(attendance);
    }

    /**
     * Fingerprint-based attendance marking. Runs 1:N matching against every enrolled
     * template in the given branch (mirrors the QR flow's branch scoping) and upserts
     * like markAttendanceByQr — a student scanning twice in one day should reconfirm,
     * not error. Rejects if no enrolled template scores above FingerprintService.MATCH_THRESHOLD,
     * or if the top two candidates are too close to call (ambiguous match).
     */
    @Transactional
    public AttendanceResponse markAttendanceByFingerprint(FingerprintAttendanceMarkRequest request, Long markedByUserId) {
        User markedBy = validateMarkerPermission(markedByUserId);

        FingerprintTemplate probe = fingerprintService.buildTemplate(request.getImageBase64(), request.getDpi());

        List<Student> candidates = studentRepository.findEnrolledFingerprintsByBranchId(request.getBranchId());

        Student bestMatch = null;
        double bestScore = -1;
        double secondBestScore = -1;
        for (Student candidate : candidates) {
            FingerprintTemplate candidateTemplate = fingerprintService.deserialize(candidate.getFingerprintTemplate());
            double score = fingerprintService.match(probe, candidateTemplate);
            if (score > bestScore) {
                secondBestScore = bestScore;
                bestScore = score;
                bestMatch = candidate;
            } else if (score > secondBestScore) {
                secondBestScore = score;
            }
        }

        if (bestMatch == null || bestScore < FingerprintService.MATCH_THRESHOLD) {
            throw new RuntimeException("Fingerprint not recognized. Please try again or contact the office.");
        }
        if (secondBestScore >= FingerprintService.MATCH_THRESHOLD && (bestScore - secondBestScore) < 5) {
            throw new RuntimeException("Fingerprint match was ambiguous. Please scan again.");
        }

        Student matchedStudent = bestMatch;
        Attendance attendance = attendanceRepository.findByStudentIdAndDate(matchedStudent.getId(), request.getDate())
                .orElseGet(() -> Attendance.builder()
                        .student(matchedStudent)
                        .date(request.getDate())
                        .build());

        attendance.setPresent(request.getPresent() != null ? request.getPresent() : true);
        attendance.setMarkedBy(markedBy);
        attendance.setMarkedAt(LocalDateTime.now());
        attendance = attendanceRepository.save(attendance);

        return toResponse(attendance);
    }

    /**
     * Shared permission check for both manual and QR marking: Admin is always
     * allowed; Staff/Teacher must have canMarkAttendance = true; anyone else rejected.
     */
    private User validateMarkerPermission(Long markedByUserId) {
        User markedBy = userRepository.findById(markedByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (markedBy.getRole() == Role.STAFF || markedBy.getRole() == Role.TEACHER) {
            Staff staff = staffRepository.findByUserId(markedByUserId)
                    .orElseThrow(() -> new RuntimeException("Staff profile not found"));
            if (!Boolean.TRUE.equals(staff.getCanMarkAttendance())) {
                throw new RuntimeException("You do not have permission to mark attendance");
            }
        } else if (markedBy.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only Admin, Teacher, or Staff can mark attendance");
        }

        return markedBy;
    }

    public List<AttendanceResponse> getAllAttendance() {
        return attendanceRepository.findAllByOrderByDateDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AttendanceResponse> getAttendanceByBranch(Long branchId) {
        return attendanceRepository.findByStudentBranchIdOrderByDateDesc(branchId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AttendanceResponse> getAttendanceByStudent(Long studentId) {
        return attendanceRepository.findByStudentIdOrderByDateDesc(studentId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AttendanceResponse> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDateOrderByStudentUserFullNameAsc(date).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AttendanceResponse> getAttendanceByBranchAndDate(Long branchId, LocalDate date) {
        return attendanceRepository.findByStudentBranchIdAndDateOrderByStudentUserFullNameAsc(branchId, date).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AttendanceResponse> getMyAttendance(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found for user"));
        return attendanceRepository.findByStudentIdOrderByDateDesc(student.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    private AttendanceResponse toResponse(Attendance attendance) {
        Student student = attendance.getStudent();
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .studentId(student.getId())
                .studentIdNo(student.getStudentIdNo())
                .studentName(student.getUser().getFullName())
                .branchName(student.getBranch().getName())
                .branchId(student.getBranch().getId())
                .date(attendance.getDate().toString())
                .present(attendance.getPresent())
                .markedByName(attendance.getMarkedBy().getFullName())
                .markedAt(attendance.getMarkedAt() != null ? attendance.getMarkedAt().toString() : null)
                .build();
    }
}