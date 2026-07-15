package com.beeline.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallmentBreakdown {
    private Integer installmentNumber;
    private Double amount;
    private String dueCycleInfo;
}
