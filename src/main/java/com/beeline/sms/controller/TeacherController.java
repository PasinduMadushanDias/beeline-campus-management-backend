package com.beeline.sms.controller;

import com.beeline.sms.dto.AnnouncementRequest;
import com.beeline.sms.dto.AnnouncementResponse;
import com.beeline.sms.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teacher")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TeacherController {

    private final AnnouncementService announcementService;

    @PostMapping("/announcements")
    public ResponseEntity<AnnouncementResponse> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(announcementService.createAnnouncement(request));
    }

    @GetMapping("/announcements")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncements(
            @RequestParam(required = false) Long branchId) {
        if (branchId != null) {
            return ResponseEntity.ok(announcementService.getAnnouncementsByBranch(branchId));
        }
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }
}
