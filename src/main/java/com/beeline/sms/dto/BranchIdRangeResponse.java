package com.beeline.sms.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BranchIdRangeResponse {
    private Long id;
    private String prefix;
    private Integer maxRange;
    private Integer currentSequence;
    private Integer remaining;
    private Boolean isActive;
}