package com.beeline.sms.controller;

import com.beeline.sms.dto.*;
import com.beeline.sms.service.BranchService;
import com.beeline.sms.service.StaffService;
import com.beeline.sms.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminController {

    private final BranchService branchService;
    private final StudentService studentService;
    private final StaffService staffService;

    // ── Branch Endpoints ──

    @PostMapping("/branches")
    public ResponseEntity<BranchResponse> createBranch(@Valid @RequestBody BranchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(branchService.createBranch(request));
    }

    @GetMapping("/branches")
    public ResponseEntity<List<BranchResponse>> getAllBranches() {
        return ResponseEntity.ok(branchService.getAllBranches());
    }

    @GetMapping("/branches/{id}")
    public ResponseEntity<BranchResponse> getBranch(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.getBranchById(id));
    }

    @PutMapping("/branches/{id}")
    public ResponseEntity<BranchResponse> updateBranch(@PathVariable Long id, @Valid @RequestBody BranchRequest request) {
        return ResponseEntity.ok(branchService.updateBranch(id, request));
    }

    @DeleteMapping("/branches/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.noContent().build();
    }

    // ── Student Endpoints ──

//    @PostMapping("/students")
//    public ResponseEntity<StudentResponse> registerStudent(@Valid @RequestBody StudentRegistrationRequest request) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.registerStudent(request));
//    }
//
//    @GetMapping("/students")
//    public ResponseEntity<List<StudentResponse>> getAllStudents() {
//        return ResponseEntity.ok(studentService.getAllStudents());
//    }
//
//    @PutMapping("/students/{id}")
//    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRegistrationRequest request) {
//        return ResponseEntity.ok(studentService.updateStudent(id, request));
//    }
//
//    @DeleteMapping("/students/{id}")
//    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
//        try {
//            studentService.deleteStudent(id);
//            return ResponseEntity.noContent().build();
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//    }

    // ── Student Endpoints ──

    @PostMapping("/students")
    public ResponseEntity<StudentResponse> registerStudent(@Valid @RequestBody StudentRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.registerStudent(request));
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentResponse>> getAllStudents(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String query) {
        if (branchId == null && (query == null || query.isBlank())) {
            return ResponseEntity.ok(studentService.getAllStudents());
        }
        return ResponseEntity.ok(studentService.searchStudents(branchId, query));
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRegistrationRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    // ── Staff / Teacher Endpoints ──

    @PostMapping("/staff")
    public ResponseEntity<StaffResponse> registerStaff(@Valid @RequestBody StaffRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(staffService.registerStaff(request));
    }

    @GetMapping("/staff")
    public ResponseEntity<List<StaffResponse>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @PutMapping("/staff/{id}")
    public ResponseEntity<StaffResponse> updateStaff(@PathVariable Long id, @Valid @RequestBody StaffRegistrationRequest request) {
        return ResponseEntity.ok(staffService.updateStaff(id, request));
    }

    @DeleteMapping("/staff/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        try {
            staffService.deleteStaff(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/staff/{id}/attendance-permission")
    public ResponseEntity<?> toggleAttendancePermission(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        try {
            Boolean canMark = body.get("canMarkAttendance");
            StaffResponse response = staffService.toggleAttendancePermission(id, canMark);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PatchMapping("/students/{id}/reset-password")
    public ResponseEntity<?> resetStudentPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String newPassword = body.get("newPassword");
            studentService.resetPassword(id, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/students/next-id")
    public ResponseEntity<?> previewNextStudentId(@RequestParam Long branchId) {
        try {
            String nextId = studentService.previewNextStudentId(branchId);
            return ResponseEntity.ok(Map.of("nextStudentId", nextId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

}
