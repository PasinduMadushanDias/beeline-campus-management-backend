package com.beeline.sms.repository;

import com.beeline.sms.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentIdOrderByDateDesc(Long studentId);
    List<Attendance> findByStudentBranchIdOrderByDateDesc(Long branchId);
    List<Attendance> findAllByOrderByDateDesc();
    List<Attendance> findByDateOrderByStudentUserFullNameAsc(LocalDate date);
    List<Attendance> findByStudentBranchIdAndDateOrderByStudentUserFullNameAsc(Long branchId, LocalDate date);
    Optional<Attendance> findByStudentIdAndDate(Long studentId, LocalDate date);
}
