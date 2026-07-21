package com.beeline.sms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceBatchMarkRequest {

    @NotNull(message = "Branch is required")
    private Long branchId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotEmpty(message = "At least one student entry is required")
    private List<@Valid Entry> entries;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Entry {
        @NotNull(message = "Student ID is required")
        private Long studentId;

        @Builder.Default
        private Boolean present = true;
    }
}
