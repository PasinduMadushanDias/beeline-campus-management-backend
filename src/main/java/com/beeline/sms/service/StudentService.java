package com.beeline.sms.service;

import com.beeline.sms.dto.StudentRegistrationRequest;
import com.beeline.sms.dto.StudentResponse;
import com.beeline.sms.entity.Branch;
import com.beeline.sms.entity.Student;
import com.beeline.sms.entity.User;
import com.beeline.sms.enums.Gender;
import com.beeline.sms.enums.Role;
import com.beeline.sms.enums.UserStatus;
import com.beeline.sms.repository.BranchRepository;
import com.beeline.sms.repository.StudentRepository;
import com.beeline.sms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    /** Matches generated IDs like A01, B99, AA01 — used to locate "the last ID" during generation. */
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^([A-Z]+)(\\d{2})$");

    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<StudentResponse> getStudentsByBranch(Long branchId) {
        return studentRepository.findByBranchId(branchId).stream()
                .map(this::toResponse)
                .toList();
    }

    /** Powers the Admin filter bar: branchId and/or a name/Student-ID query, either optional. */
    public List<StudentResponse> searchStudents(Long branchId, String query) {
        String normalizedQuery = (query == null || query.isBlank()) ? null : query.trim().toLowerCase();
        return studentRepository.search(branchId, normalizedQuery).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public StudentResponse registerStudent(StudentRegistrationRequest request) {
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + request.getBranchId()));

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .role(Role.STUDENT)
                .status(UserStatus.ACTIVE)
                .build();
        user = userRepository.save(user);

        Student student = Student.builder()
                .user(user)
                .branch(branch)
                .studentIdNo(generateStudentIdNo(branch.getId())) // now scoped to this branch's own sequence
                .address(request.getAddress())
                .telephone(request.getTelephone())
                .birthday(request.getBirthday())
                .gender(request.getGender())
                .nic(request.getNic())
                .build();
        student = studentRepository.save(student);

        return toResponse(student);
    }

    @Transactional
    public StudentResponse updateStudent(Long id, StudentRegistrationRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + request.getBranchId()));

        User user = student.getUser();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        userRepository.save(user);

        student.setBranch(branch);
        student.setAddress(request.getAddress());
        student.setTelephone(request.getTelephone());
        student.setBirthday(request.getBirthday());
        student.setGender(request.getGender());
        student.setNic(request.getNic());
        // studentIdNo is intentionally never touched here — it's immutable after creation,
        // and re-generating it on a branch change would risk colliding with an existing
        // ID already assigned in the new branch. If cross-branch transfer needs a new ID,
        // that should be a deliberate, separate operation — not a side effect of an edit.
        studentRepository.save(student);

        return toResponse(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        User user = student.getUser();
        studentRepository.delete(student);
        userRepository.delete(user);
    }

    /**
     * Generates the next sequential ID within a single branch's own sequence: A01 ... A99, then
     * B01 ... B99, etc. Two different branches independently maintain their own sequence, so
     * Galle's A01 and Matara's A01 coexisting is expected — enforced-safe via the
     * (branch_id, student_id_no) composite unique constraint on the students table.
     * Retries on the rare chance of a collision under concurrent registrations to the same branch.
     */
    private String generateStudentIdNo(Long branchId) {
        List<String> existingIdsForBranch = studentRepository.findStudentIdNosByBranchId(branchId);
        String candidate = computeNextStudentId(existingIdsForBranch);

        int attempts = 0;
        while (studentRepository.existsByBranchIdAndStudentIdNo(branchId, candidate) && attempts < 5) {
            existingIdsForBranch.add(candidate);
            candidate = computeNextStudentId(existingIdsForBranch);
            attempts++;
        }
        return candidate;
    }

    private String computeNextStudentId(List<String> existingIds) {
        String currentMax = existingIds.stream()
                .filter(id -> id != null && STUDENT_ID_PATTERN.matcher(id).matches())
                .max(Comparator.comparingLong(this::rankStudentId))
                .orElse(null);

        if (currentMax == null) {
            return "A01";
        }

        Matcher matcher = STUDENT_ID_PATTERN.matcher(currentMax);
        matcher.matches();
        String letters = matcher.group(1);
        int number = Integer.parseInt(matcher.group(2));

        if (number >= 99) {
            return incrementLetters(letters) + "01";
        }
        return letters + String.format("%02d", number + 1);
    }

    private long rankStudentId(String id) {
        Matcher matcher = STUDENT_ID_PATTERN.matcher(id);
        matcher.matches();
        String letters = matcher.group(1);
        int number = Integer.parseInt(matcher.group(2));

        long letterValue = 0;
        for (char c : letters.toCharArray()) {
            letterValue = letterValue * 26 + (c - 'A' + 1);
        }
        return letterValue * 100 + number;
    }

    /** Excel-style column increment: A -> B ... Z -> AA -> AB ... */
    private String incrementLetters(String letters) {
        char[] chars = letters.toCharArray();
        int i = chars.length - 1;
        while (i >= 0) {
            if (chars[i] != 'Z') {
                chars[i]++;
                return new String(chars);
            }
            chars[i] = 'A';
            i--;
        }
        return "A" + new String(chars);
    }

    private StudentResponse toResponse(Student student) {
        User user = student.getUser();
        Branch branch = student.getBranch();
        return StudentResponse.builder()
                .id(student.getId())
                .userId(user.getId())
                .studentIdNo(student.getStudentIdNo())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .branchId(branch != null ? branch.getId() : null)
                .branchName(branch != null ? branch.getName() : null)
                .totalFee(branch != null ? branch.getTotalFee() : null)
                .enrollmentDate(student.getEnrollmentDate() != null ? student.getEnrollmentDate().toString() : null)
                .address(student.getAddress())
                .telephone(student.getTelephone())
                .birthday(student.getBirthday() != null ? student.getBirthday().toString() : null)
                .gender(student.getGender() != null ? student.getGender().name() : null)
                .nic(student.getNic())
                .build();
    }

    public String previewNextStudentId(Long branchId) {
        branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + branchId));

        List<String> existingIdsForBranch = studentRepository.findStudentIdNosByBranchId(branchId);
        return computeNextStudentId(existingIdsForBranch);
    }

    public StudentResponse getMyProfile(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found for user id: " + userId));
        return toResponse(student);
    }


}