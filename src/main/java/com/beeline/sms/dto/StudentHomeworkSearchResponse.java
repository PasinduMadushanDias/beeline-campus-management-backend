package com.beeline.sms.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentHomeworkSearchResponse {
    private Long studentId;
    private String studentIdNo;
    private String studentName;
    private String branchName;
    private String date;
    private boolean attendanceMarked;
    private List<TaskSubmissionEntry> taskSubmissions;
    private List<TaskSubmissionEntry> previousIncomplete;

    @Data
    @Builder
    public static class TaskSubmissionEntry {
        private Long submissionId;
        private Long homeworkTaskId;
        private String taskDetails;
        private String assignedDate;
        private String status;
        private String checkedByName;
        private String checkedAt;
    }
}
