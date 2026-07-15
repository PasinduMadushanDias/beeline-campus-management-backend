package com.beeline.sms.controller;

import com.beeline.sms.dto.*;
import com.beeline.sms.service.HomeworkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/homework")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;

    @PostMapping("/assign")
    public ResponseEntity<?> assignHomework(@Valid @RequestBody HomeworkTaskRequest request) {
        try {
            return ResponseEntity.status(201).body(homeworkService.assignHomework(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/branch/{branchId}/date/{date}")
    public ResponseEntity<HomeworkBranchDateResponse> getByBranchAndDate(
            @PathVariable Long branchId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(homeworkService.getHomeworkByBranchAndDate(branchId, date));
    }

    @GetMapping("/student-view/branch/{branchId}/date/{date}")
    public ResponseEntity<?> getStudentView(
            @PathVariable Long branchId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long userId) {
        try {
            return ResponseEntity.ok(homeworkService.getStudentViewByBranchAndDate(branchId, date, userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/search-student")
    public ResponseEntity<?> searchStudentHomework(
            @RequestParam Long branchId,
            @RequestParam String studentIdNo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            return ResponseEntity.ok(homeworkService.searchStudentHomework(branchId, studentIdNo, date));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/submit-status")
    public ResponseEntity<?> batchUpdateStatus(
            @RequestParam Long checkedByUserId,
            @Valid @RequestBody BatchStatusUpdateRequest request) {
        try {
            return ResponseEntity.ok(homeworkService.batchUpdateStatus(request, checkedByUserId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<HomeworkTaskResponse>> getAllTasks(
            @RequestParam(required = false) Long branchId) {
        return ResponseEntity.ok(homeworkService.getAllTasks(branchId));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyHomework(@RequestParam Long userId) {
        try {
            return ResponseEntity.ok(homeworkService.getMyHomework(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
