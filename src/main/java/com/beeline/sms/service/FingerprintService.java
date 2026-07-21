package com.beeline.sms.service;

import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintImageOptions;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * Wraps SourceAFIS template extraction/matching behind one place, so enrollment
 * (StudentService) and verification (AttendanceService) never diverge in how a
 * raw scan becomes a template or what score counts as a match.
 *
 * MATCH_THRESHOLD = 40 is SourceAFIS's own documented cutoff for a low false-accept
 * rate; raise it if a client reports false-positive matches in practice.
 */
@Service
public class FingerprintService {

    public static final double MATCH_THRESHOLD = 40.0;

    /** Builds a template straight from the raw grayscale scan bytes sent by the scanner agent. */
    public FingerprintTemplate buildTemplate(String imageBase64, Integer dpi) {
        byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
        FingerprintImageOptions options = new FingerprintImageOptions()
                .dpi(dpi != null ? dpi : 500);
        FingerprintImage image = new FingerprintImage(imageBytes, options);
        return new FingerprintTemplate(image);
    }

    /**
     * Serializes a template to Base64 text for a TEXT column. toByteArray() returns
     * compressed binary (not text) — storing it as a raw String corrupts it, since
     * arbitrary bytes aren't valid UTF-8 (Postgres rejects the embedded 0x00s).
     */
    public String serialize(FingerprintTemplate template) {
        return Base64.getEncoder().encodeToString(template.toByteArray());
    }

    public FingerprintTemplate deserialize(String serialized) {
        return new FingerprintTemplate(Base64.getDecoder().decode(serialized));
    }

    /** Score between two templates — higher means more similar. Compare against MATCH_THRESHOLD. */
    public double match(FingerprintTemplate probe, FingerprintTemplate candidate) {
        return new FingerprintMatcher(probe).match(candidate);
    }
}
