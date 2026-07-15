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
public class StaffResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String username;
    private String email;
    private String role;
    private String status;
    private Boolean canMarkAttendance;
    private List<BranchInfo> branches;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BranchInfo {
        private Long id;
        private String name;
    }
}
