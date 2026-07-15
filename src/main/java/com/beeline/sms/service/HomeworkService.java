package com.beeline.sms.service;

import com.beeline.sms.dto.*;
import com.beeline.sms.entity.*;
import com.beeline.sms.enums.HomeworkStatus;
import com.beeline.sms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeworkService {

    private final HomeworkTaskRepository homeworkTaskRepository;
    private final StudentHomeworkSubmissionRepository submissionRepository;
    private final StudentRepository studentRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;

    private boolean isAttendanceMarked(Long studentId, LocalDate date) {
        return attendanceRepository.findByStudentIdAndDate(studentId, date).isPresent();
    }

    @Transactional
    public List<HomeworkTaskResponse> assignHomework(HomeworkTaskRequest request) {
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + request.getBranchId()));

        List<Student> students = studentRepository.findByBranchId(request.getBranchId());
        List<HomeworkTaskResponse> responses = new ArrayList<>();

        for (String taskDetail : request.getTasks()) {
            HomeworkTask task = HomeworkTask.builder()
                    .branch(branch)
                    .assignedDate(request.getAssignedDate())
                    .taskDetails(taskDetail.trim())
                    .build();
            task = homeworkTaskRepository.save(task);

            for (Student student : students) {
                StudentHomeworkSubmission submission = StudentHomeworkSubmission.builder()
                        .student(student)
                        .homeworkTask(task)
                        .status(HomeworkStatus.PENDING)
                        .build();
                submissionRepository.save(submission);
            }

            responses.add(toTaskResponse(task));
        }

        return responses;
    }

    public List<HomeworkTaskResponse> getAllTasks(Long branchId) {
        List<HomeworkTask> tasks;
        if (branchId != null) {
            tasks = homeworkTaskRepository.findByBranchIdOrderByAssignedDateDesc(branchId);
        } else {
            tasks = homeworkTaskRepository.findAllByOrderByAssignedDateDesc();
        }
        return tasks.stream().map(this::toTaskResponse).toList();
    }

    public HomeworkBranchDateResponse getHomeworkByBranchAndDate(Long branchId, LocalDate date) {
        List<HomeworkTask> tasks = homeworkTaskRepository.findByBranchIdAndAssignedDate(branchId, date);

        if (tasks.isEmpty()) {
            return HomeworkBranchDateResponse.builder()
                    .tasks(List.of())
                    .submissions(List.of())
                    .build();
        }

        List<HomeworkTaskResponse> taskResponses = tasks.stream().map(this::toTaskResponse).toList();

        List<StudentSubmissionResponse> submissions =
                submissionRepository.findByHomeworkTaskInOrderByStudentUserFullNameAsc(tasks)
                        .stream()
                        .map(this::toSubmissionResponse)
                        .toList();

        return HomeworkBranchDateResponse.builder()
                .tasks(taskResponses)
                .submissions(submissions)
                .build();
    }

    public StudentHomeworkSearchResponse searchStudentHomework(Long branchId, String studentIdNo, LocalDate date) {
        Student student = studentRepository.findByBranchIdAndStudentIdNo(branchId, studentIdNo)
                .orElseThrow(() -> new RuntimeException(
                        "Student not found with ID: " + studentIdNo + " in the selected branch"));

        List<HomeworkTask> tasks = homeworkTaskRepository.findByBranchIdAndAssignedDate(
                student.getBranch().getId(), date);

        boolean attendanceMarked = isAttendanceMarked(student.getId(), date);

        List<StudentHomeworkSearchResponse.TaskSubmissionEntry> entries = new ArrayList<>();

        for (HomeworkTask task : tasks) {
            StudentHomeworkSubmission sub = submissionRepository
                    .findByStudentIdAndHomeworkTaskId(student.getId(), task.getId())
                    .orElse(null);

            entries.add(StudentHomeworkSearchResponse.TaskSubmissionEntry.builder()
                    .submissionId(sub != null ? sub.getId() : null)
                    .homeworkTaskId(task.getId())
                    .taskDetails(task.getTaskDetails())
                    .assignedDate(task.getAssignedDate().toString())
                    .status(sub != null ? sub.getStatus().name() : "PENDING")
                    .checkedByName(sub != null && sub.getCheckedBy() != null ? sub.getCheckedBy().getFullName() : null)
                    .checkedAt(sub != null && sub.getCheckedAt() != null ? sub.getCheckedAt().toString() : null)
                    .build());
        }

        List<StudentHomeworkSearchResponse.TaskSubmissionEntry> previousIncomplete = submissionRepository
                .findByStudentIdAndStatusAndHomeworkTask_AssignedDateBeforeOrderByHomeworkTask_AssignedDateDesc(
                        student.getId(), HomeworkStatus.INCOMPLETE, date)
                .stream()
                .map(sub -> StudentHomeworkSearchResponse.TaskSubmissionEntry.builder()
                        .submissionId(sub.getId())
                        .homeworkTaskId(sub.getHomeworkTask().getId())
                        .taskDetails(sub.getHomeworkTask().getTaskDetails())
                        .assignedDate(sub.getHomeworkTask().getAssignedDate().toString())
                        .status(sub.getStatus().name())
                        .checkedByName(sub.getCheckedBy() != null ? sub.getCheckedBy().getFullName() : null)
                        .checkedAt(sub.getCheckedAt() != null ? sub.getCheckedAt().toString() : null)
                        .build())
                .toList();

        return StudentHomeworkSearchResponse.builder()
                .studentId(student.getId())
                .studentIdNo(student.getStudentIdNo())
                .studentName(student.getUser().getFullName())
                .branchName(student.getBranch().getName())
                .date(date.toString())
                .attendanceMarked(attendanceMarked)
                .taskSubmissions(entries)
                .previousIncomplete(previousIncomplete)
                .build();
    }

    @Transactional
    public List<StudentSubmissionResponse> batchUpdateStatus(BatchStatusUpdateRequest request, Long checkedByUserId) {
        User checkedBy = userRepository.findById(checkedByUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + checkedByUserId));

        return request.getUpdates().stream().map(entry -> {
            StudentHomeworkSubmission submission = submissionRepository.findById(entry.getSubmissionId())
                    .orElseThrow(() -> new RuntimeException(
                            "Submission not found with id: " + entry.getSubmissionId()));

            Student student = submission.getStudent();
            LocalDate date = submission.getHomeworkTask().getAssignedDate();
            if (!isAttendanceMarked(student.getId(), date)) {
                throw new RuntimeException(
                        "Cannot update homework status: attendance has not been marked for "
                                + student.getUser().getFullName() + " on " + date);
            }

            submission.setStatus(HomeworkStatus.valueOf(entry.getStatus().toUpperCase()));
            submission.setCheckedBy(checkedBy);
            submission.setCheckedAt(LocalDateTime.now());

            return toSubmissionResponse(submissionRepository.save(submission));
        }).toList();
    }

    public List<StudentSubmissionResponse> getStudentViewByBranchAndDate(Long branchId, LocalDate date, Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student not found for user id: " + userId));

        List<HomeworkTask> tasks = homeworkTaskRepository.findByBranchIdAndAssignedDate(branchId, date);

        if (tasks.isEmpty()) {
            return List.of();
        }

        return submissionRepository.findByStudentIdAndHomeworkTaskIn(student.getId(), tasks)
                .stream()
                .map(this::toSubmissionResponse)
                .toList();
    }

    public List<StudentSubmissionResponse> getMyHomework(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student not found for user id: " + userId));

        return submissionRepository.findByStudentId(student.getId())
                .stream()
                .map(this::toSubmissionResponse)
                .toList();
    }

    private HomeworkTaskResponse toTaskResponse(HomeworkTask task) {
        return HomeworkTaskResponse.builder()
                .id(task.getId())
                .branchId(task.getBranch().getId())
                .branchName(task.getBranch().getName())
                .assignedDate(task.getAssignedDate().toString())
                .taskDetails(task.getTaskDetails())
                .build();
    }

    private StudentSubmissionResponse toSubmissionResponse(StudentHomeworkSubmission sub) {
        return StudentSubmissionResponse.builder()
                .id(sub.getId())
                .studentId(sub.getStudent().getId())
                .studentIdNo(sub.getStudent().getStudentIdNo())
                .studentName(sub.getStudent().getUser().getFullName())
                .homeworkTaskId(sub.getHomeworkTask().getId())
                .status(sub.getStatus().name())
                .checkedByName(sub.getCheckedBy() != null ? sub.getCheckedBy().getFullName() : null)
                .checkedAt(sub.getCheckedAt() != null ? sub.getCheckedAt().toString() : null)
                .assignedDate(sub.getHomeworkTask().getAssignedDate().toString())
                .taskDetails(sub.getHomeworkTask().getTaskDetails())
                .branchName(sub.getHomeworkTask().getBranch().getName())
                .build();
    }
}
