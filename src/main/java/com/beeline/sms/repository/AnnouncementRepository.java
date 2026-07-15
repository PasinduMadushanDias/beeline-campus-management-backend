package com.beeline.sms.repository;

import com.beeline.sms.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByTargetBranchIdOrTargetBranchIsNull(Long branchId);
    List<Announcement> findAllByOrderByPostedAtDesc();
}
