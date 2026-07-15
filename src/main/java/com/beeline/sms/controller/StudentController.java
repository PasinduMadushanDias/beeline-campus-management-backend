package com.beeline.sms.controller;

import com.beeline.sms.dto.InstallmentBreakdown;
import com.beeline.sms.dto.StudentResponse;
import com.beeline.sms.service.FeeService;
import com.beeline.sms.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
