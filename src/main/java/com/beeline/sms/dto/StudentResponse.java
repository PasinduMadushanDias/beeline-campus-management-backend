package com.beeline.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {
    private Long id;
    private Long userId;
    private String studentIdNo;
    private String fullName;
    private String username;
    private String email;
    private String status;
    private Long branchId;
    private String branchName;
    private Double totalFee;
    private String enrollmentDate;

    private String address;
    private String telephone;
    private String birthday;
    private String gender;
    private String nic;
}