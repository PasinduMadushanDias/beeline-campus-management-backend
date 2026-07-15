package com.beeline.sms.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkTaskResponse {
    private Long id;
    private Long branchId;
    private String branchName;
    private String assignedDate;
    private String taskDetails;
}
