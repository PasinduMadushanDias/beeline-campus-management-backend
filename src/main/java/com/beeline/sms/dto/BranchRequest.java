package com.beeline.sms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchRequest {

    @NotBlank(message = "Branch name is required")
    private String name;

    private String duration;

    private String schedule;

    private String color;

    @NotNull(message = "Total fee is required")
    @Min(value = 0, message = "Total fee must be non-negative")
    private Double totalFee;

    @NotNull(message = "Installments count is required")
    @Min(value = 1, message = "At least 1 installment required")
    private Integer installmentsCount;

    private String dueDayValue;
}
