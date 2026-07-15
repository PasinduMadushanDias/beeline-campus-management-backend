package com.beeline.sms.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HomeworkBranchDateResponse {
    private List<HomeworkTaskResponse> tasks;
    private List<StudentSubmissionResponse> submissions;
}
