package com.beeline.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchResponse {
    private Long id;
    private String name;
    private String duration;
    private String schedule;
    private String color;
    private Double totalFee;
    private Integer installmentsCount;
    private String dueDayValue;
}
