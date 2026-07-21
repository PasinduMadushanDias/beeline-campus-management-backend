package com.beeline.sms.controller;

import com.beeline.sms.dto.AttendanceBatchMarkRequest;
import com.beeline.sms.dto.AttendanceMarkRequest;
import com.beeline.sms.dto.AttendanceResponse;
import com.beeline.sms.dto.FingerprintAttendanceMarkRequest;
import com.beeline.sms.dto.QrAttendanceMarkRequest;
import com.beeline.sms.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/mark-by-id")
    public ResponseEntity<?> markAttendance(
            @Valid @RequestBody AttendanceMarkRequest request,
            @RequestParam Long markedByUserId) {
        try {
            AttendanceResponse response = attendanceService.markAttendanceByStudentId(request, markedByUserId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/mark-by-qr")
    public ResponseEntity<?> markAttendanceByQr(
            @Valid @RequestBody QrAttendanceMarkRequest request,
            @RequestParam Long markedByUserId) {
        try {
            AttendanceResponse response = attendanceService.markAttendanceByQr(request, markedByUserId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/mark-by-fingerprint")
    public ResponseEntity<?> markAttendanceByFingerprint(
            @Valid @RequestBody FingerprintAttendanceMarkRequest request,
            @RequestParam Long markedByUserId) {
        try {
            AttendanceResponse response = attendanceService.markAttendanceByFingerprint(request, markedByUserId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/mark-batch")
    public ResponseEntity<?> markAttendanceBatch(
            @Valid @RequestBody AttendanceBatchMarkRequest request,
            @RequestParam Long markedByUserId) {
        try {
            List<AttendanceResponse> response = attendanceService.markAttendanceBatch(request, markedByUserId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Page<AttendanceResponse>> getAllAttendance(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (branchId != null && date != null) {
            return ResponseEntity.ok(attendanceService.getAttendanceByBranchAndDate(branchId, LocalDate.parse(date), pageable));
        }
        if (branchId != null) {
            return ResponseEntity.ok(attendanceService.getAttendanceByBranch(branchId, pageable));
        }
        if (date != null) {
            return ResponseEntity.ok(attendanceService.getAttendanceByDate(LocalDate.parse(date), pageable));
        }
        return ResponseEntity.ok(attendanceService.getAllAttendance(pageable));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AttendanceResponse>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByStudent(studentId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<AttendanceResponse>> getMyAttendance(@RequestParam Long userId) {
        return ResponseEntity.ok(attendanceService.getMyAttendance(userId));
    }
}