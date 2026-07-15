package com.beeline.sms.repository;

import com.beeline.sms.entity.HomeworkTask;
import com.beeline.sms.entity.StudentHomeworkSubmission;
import com.beeline.sms.enums.HomeworkStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentHomeworkSubmissionRepository extends JpaRepository<StudentHomeworkSubmission, Long> {

    List<StudentHomeworkSubmission> findByHomeworkTaskId(Long homeworkTaskId);

    List<StudentHomeworkSubmission> findByStudentId(Long studentId);

    Optional<StudentHomeworkSubmission> findByStudentIdAndHomeworkTaskId(Long studentId, Long homeworkTaskId);

    List<StudentHomeworkSubmission> findByHomeworkTaskIdOrderByStudentUserFullNameAsc(Long homeworkTaskId);

    List<StudentHomeworkSubmission> findByStudentIdAndHomeworkTaskIn(Long studentId, List<HomeworkTask> tasks);

    List<StudentHomeworkSubmission> findByHomeworkTaskInOrderByStudentUserFullNameAsc(List<HomeworkTask> tasks);

    List<StudentHomeworkSubmission> findByStudentIdAndStatusAndHomeworkTask_AssignedDateBeforeOrderByHomeworkTask_AssignedDateDesc(
            Long studentId, HomeworkStatus status, LocalDate date);
}
