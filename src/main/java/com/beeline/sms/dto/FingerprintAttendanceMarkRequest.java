package com.beeline.sms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request body for fingerprint-based attendance marking. Identifies the student
 * by 1:N template matching within the given branch (mirrors QrAttendanceMarkRequest's
 * branch-scoping rationale: matching within one branch is faster and keeps students
 * with visually similar scans from different branches from being confused).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FingerprintAttendanceMarkRequest {

    @NotNull
    private Long branchId;

    @NotBlank(message = "Fingerprint image is required")
    private String imageBase64;

    @Builder.Default
    private Integer dpi = 500;

    @NotNull
    private LocalDate date;

    private Boolean present;
}
