package com.beeline.sms.repository;

import com.beeline.sms.entity.HomeworkTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HomeworkTaskRepository extends JpaRepository<HomeworkTask, Long> {

    List<HomeworkTask> findByBranchIdAndAssignedDate(Long branchId, LocalDate assignedDate);

    List<HomeworkTask> findByBranchIdOrderByAssignedDateDesc(Long branchId);

    List<HomeworkTask> findAllByOrderByAssignedDateDesc();
}
