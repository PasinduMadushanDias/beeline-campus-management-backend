package com.beeline.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponse {
    private Long id;
    private Long studentId;
    private String studentIdNo;
    private String studentName;
    private String branchName;
    private Long branchId;
    private String date;
    private Boolean present;
    private String markedByName;
    private String markedAt;
}
