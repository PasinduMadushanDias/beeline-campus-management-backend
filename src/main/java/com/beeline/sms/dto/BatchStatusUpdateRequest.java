package com.beeline.sms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BatchStatusUpdateRequest {

    @NotNull
    private List<StatusEntry> updates;

    @Data
    public static class StatusEntry {
        @NotNull
        private Long submissionId;
        @NotNull
        private String status;
    }
}
