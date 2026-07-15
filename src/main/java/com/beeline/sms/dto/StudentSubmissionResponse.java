package com.beeline.sms.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentSubmissionResponse {
    private Long id;
    private Long studentId;
    private String studentIdNo;
    private String studentName;
    private Long homeworkTaskId;
    private String status;
    private String checkedByName;
    private String checkedAt;
    private String assignedDate;
    private String taskDetails;
    private String branchName;
}
