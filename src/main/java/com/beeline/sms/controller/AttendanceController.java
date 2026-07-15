package com.beeline.sms.controller;

import com.beeline.sms.dto.AttendanceMarkRequest;
import com.beeline.sms.dto.AttendanceResponse;
import com.beeline.sms.dto.QrAttendanceMarkRequest;
import com.beeline.sms.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAllAttendance(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String date) {
        if (branchId != null && date != null) {
            return ResponseEntity.ok(attendanceService.getAttendanceByBranchAndDate(branchId, LocalDate.parse(date)));
        }
        if (branchId != null) {
            return ResponseEntity.ok(attendanceService.getAttendanceByBranch(branchId));
        }
        if (date != null) {
            return ResponseEntity.ok(attendanceService.getAttendanceByDate(LocalDate.parse(date)));
        }
        return ResponseEntity.ok(attendanceService.getAllAttendance());
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