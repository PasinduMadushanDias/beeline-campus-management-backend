package com.beeline.sms.service;

import com.beeline.sms.dto.AnnouncementRequest;
import com.beeline.sms.dto.AnnouncementResponse;
import com.beeline.sms.entity.Announcement;
import com.beeline.sms.entity.Branch;
import com.beeline.sms.entity.User;
import com.beeline.sms.repository.AnnouncementRepository;
import com.beeline.sms.repository.BranchRepository;
import com.beeline.sms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    public List<AnnouncementResponse> getAllAnnouncements() {
        return announcementRepository.findAllByOrderByPostedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AnnouncementResponse> getAnnouncementsByBranch(Long branchId) {
        return announcementRepository.findByTargetBranchIdOrTargetBranchIsNull(branchId).stream()
                .map(this::toResponse)
                .toList();
    }

    public AnnouncementResponse createAnnouncement(AnnouncementRequest request) {
        User author = userRepository.findById(request.getPostedByUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getPostedByUserId()));

        Branch targetBranch = null;
        if (request.getTargetBranchId() != null) {
            targetBranch = branchRepository.findById(request.getTargetBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found with id: " + request.getTargetBranchId()));
        }

        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .postedBy(author)
                .targetBranch(targetBranch)
                .build();

        return toResponse(announcementRepository.save(announcement));
    }

    private AnnouncementResponse toResponse(Announcement a) {
        return AnnouncementResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .content(a.getContent())
                .postedAt(a.getPostedAt() != null ? a.getPostedAt().toString() : null)
                .authorName(a.getPostedBy().getFullName())
                .targetBranch(a.getTargetBranch() != null ? a.getTargetBranch().getName() : "Both")
                .build();
    }
}
