package com.beeline.sms.service;

import com.beeline.sms.dto.InstallmentBreakdown;
import com.beeline.sms.entity.Branch;
import com.beeline.sms.entity.Student;
import com.beeline.sms.repository.BranchRepository;
import com.beeline.sms.repository.StudentRepository;
import com.beeline.sms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeService {

    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public List<InstallmentBreakdown> getInstallmentsByBranch(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + branchId));
        return computeInstallments(branch);
    }

    public List<InstallmentBreakdown> getInstallmentsByUser(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student record not found for user id: " + userId));
        if (student.getBranch() == null) {
            throw new RuntimeException("Student is not assigned to any branch");
        }
        return computeInstallments(student.getBranch());
    }

    private List<InstallmentBreakdown> computeInstallments(Branch branch) {
        double totalFee = branch.getTotalFee();
        int count = branch.getInstallmentsCount();
        String dueCycleInfo = branch.getDueDayValue();

        double base = Math.floor(totalFee / count);
        double remainder = totalFee - base * count;

        List<InstallmentBreakdown> installments = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            installments.add(InstallmentBreakdown.builder()
                    .installmentNumber(i + 1)
                    .amount(i == 0 ? base + remainder : base)
                    .dueCycleInfo(dueCycleInfo)
                    .build());
        }
        return installments;
    }
}
