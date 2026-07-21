package com.beeline.sms.repository;

import com.beeline.sms.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentIdOrderByDateDesc(Long studentId);
    List<Attendance> findByStudentBranchIdOrderByDateDesc(Long branchId);
    Optional<Attendance> findByStudentIdAndDate(Long studentId, LocalDate date);

    Page<Attendance> findAllByOrderByDateDesc(Pageable pageable);
    Page<Attendance> findByStudentBranchIdOrderByDateDesc(Long branchId, Pageable pageable);
    Page<Attendance> findByDateOrderByStudentUserFullNameAsc(LocalDate date, Pageable pageable);
    Page<Attendance> findByStudentBranchIdAndDateOrderByStudentUserFullNameAsc(Long branchId, LocalDate date, Pageable pageable);
}
