package com.beeline.sms.dto;

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
public class AnnouncementRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String content;

    @NotNull(message = "Posted by user ID is required")
    private Long postedByUserId;

    private Long targetBranchId;
}
