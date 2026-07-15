package com.beeline.sms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request body for QR-based attendance marking. Kept separate from
 * AttendanceMarkRequest (used by manual mark-by-id) because the QR flow
 * identifies the student by (branchId, studentIdNo) instead of a bare
 * studentIdNo, which is only unique within a branch.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrAttendanceMarkRequest {

    @NotNull
    private Long branchId;

    @NotNull
    private String studentIdNo;

    @NotNull
    private LocalDate date;

    private Boolean present;
}