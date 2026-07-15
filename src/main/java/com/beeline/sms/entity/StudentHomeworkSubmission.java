package com.beeline.sms.entity;

import com.beeline.sms.enums.HomeworkStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_homework_submissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "homework_task_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentHomeworkSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homework_task_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private HomeworkTask homeworkTask;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private HomeworkStatus status = HomeworkStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_by_user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User checkedBy;

    private LocalDateTime checkedAt;
}
