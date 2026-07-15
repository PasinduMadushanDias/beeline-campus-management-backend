package com.beeline.sms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceMarkRequest {

    @NotNull(message = "Branch is required")
    private Long branchId;

    @NotBlank(message = "Student ID number is required")
    private String studentIdNo;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Builder.Default
    private Boolean present = true;
}
