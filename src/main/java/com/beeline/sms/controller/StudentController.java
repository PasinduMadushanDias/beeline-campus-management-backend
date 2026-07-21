package com.beeline.sms.controller;

import com.beeline.sms.dto.ChangePasswordRequest;
import com.beeline.sms.dto.FingerprintEnrollRequest;
import com.beeline.sms.dto.InstallmentBreakdown;
import com.beeline.sms.dto.StudentResponse;
import com.beeline.sms.service.FeeService;
import com.beeline.sms.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/student")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StudentController {

    private final FeeService feeService;
    private final StudentService studentService;

    @GetMapping("/fees")
    public ResponseEntity<List<InstallmentBreakdown>> getFeesByBranch(@RequestParam Long branchId) {
        return ResponseEntity.ok(feeService.getInstallmentsByBranch(branchId));
    }

    @GetMapping("/fees/my")
    public ResponseEntity<List<InstallmentBreakdown>> getMyFees(@RequestParam Long userId) {
        return ResponseEntity.ok(feeService.getInstallmentsByUser(userId));
    }

    @GetMapping("/profile")
    public ResponseEntity<StudentResponse> getMyProfile(@RequestParam Long userId) {
        return ResponseEntity.ok(studentService.getMyProfile(userId));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            studentService.changePassword(request);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/fingerprint")
    public ResponseEntity<?> enrollFingerprint(@PathVariable Long id, @Valid @RequestBody FingerprintEnrollRequest request) {
        try {
            studentService.enrollFingerprint(id, request);
            return ResponseEntity.ok(Map.of("message", "Fingerprint enrolled successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/fingerprint")
    public ResponseEntity<?> removeFingerprint(@PathVariable Long id) {
        try {
            studentService.removeFingerprint(id);
            return ResponseEntity.ok(Map.of("message", "Fingerprint removed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
