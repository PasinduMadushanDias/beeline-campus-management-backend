package com.beeline.sms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Sent by the local scanner agent (running on the front-desk PC) during enrollment.
 * The agent forwards the raw grayscale scan straight from the SecuGen SDK — the
 * server is the single place that turns raw scans into SourceAFIS templates, so
 * enrollment and verification always use the same extraction logic/version.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FingerprintEnrollRequest {

    @NotBlank(message = "Fingerprint image is required")
    private String imageBase64;

    @Builder.Default
    private Integer dpi = 500;
}
