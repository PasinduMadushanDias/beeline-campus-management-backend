package com.beeline.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Long id;
    private String fullName;
    private String username;
    private String role;
    private String status;
    private Boolean canMarkAttendance;
    private List<BranchAssignment> branches;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BranchAssignment {
        private Long id;
        private String name;
    }
}
