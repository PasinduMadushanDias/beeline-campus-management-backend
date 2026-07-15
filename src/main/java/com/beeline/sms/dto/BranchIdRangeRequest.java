package com.beeline.sms.dto;

import lombok.Data;

@Data
public class BranchIdRangeRequest {
    private Long id;          // null when pre-allocating a brand-new range; set when editing an existing one
    private String prefix;    // required for new ranges — must be exactly the next letter (validated server-side)
    private Integer maxRange; // required, > 0, and >= currentSequence when editing an existing range
    private Boolean isActive; // exactly one range per branch must be true
}