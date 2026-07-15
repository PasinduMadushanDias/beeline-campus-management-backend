package com.beeline.sms.repository;

import com.beeline.sms.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByBranchId(Long branchId);

    Optional<Student> findByUserId(Long userId);

    Optional<Student> findByStudentIdNo(String studentIdNo);

    Optional<Student> findByBranchIdAndStudentIdNo(Long branchId, String studentIdNo);

    boolean existsByBranchIdAndStudentIdNo(Long branchId, String studentIdNo);

    @Query("SELECT s.studentIdNo FROM Student s WHERE s.branch.id = :branchId")
    List<String> findStudentIdNosByBranchId(@Param("branchId") Long branchId);

    // Kept for reference / any other callers, but no longer used by ID generation
    boolean existsByStudentIdNo(String studentIdNo);

    @Query("SELECT s.studentIdNo FROM Student s")
    List<String> findAllStudentIdNos();

    @Query("SELECT s FROM Student s WHERE " +
            "(:branchId IS NULL OR s.branch.id = :branchId) AND " +
            "(:query IS NULL OR LOWER(s.user.fullName) LIKE CONCAT('%', :query, '%') " +
            "OR LOWER(s.studentIdNo) LIKE CONCAT('%', :query, '%'))")
    List<Student> search(@Param("branchId") Long branchId, @Param("query") String query);
}