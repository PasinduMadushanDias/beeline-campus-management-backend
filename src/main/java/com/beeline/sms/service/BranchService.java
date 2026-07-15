package com.beeline.sms.service;

import com.beeline.sms.dto.BranchRequest;
import com.beeline.sms.dto.BranchResponse;
import com.beeline.sms.entity.Branch;
import com.beeline.sms.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    public List<BranchResponse> getAllBranches() {
        return branchRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public BranchResponse getBranchById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + id));
        return toResponse(branch);
    }

    public BranchResponse createBranch(BranchRequest request) {
        Branch branch = Branch.builder()
                .name(request.getName())
                .duration(request.getDuration())
                .schedule(request.getSchedule())
                .color(request.getColor())
                .totalFee(request.getTotalFee())
                .installmentsCount(request.getInstallmentsCount())
                .dueDayValue(request.getDueDayValue())
                .build();
        return toResponse(branchRepository.save(branch));
    }

    public BranchResponse updateBranch(Long id, BranchRequest request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + id));
        branch.setName(request.getName());
        branch.setDuration(request.getDuration());
        branch.setSchedule(request.getSchedule());
        branch.setColor(request.getColor());
        branch.setTotalFee(request.getTotalFee());
        branch.setInstallmentsCount(request.getInstallmentsCount());
        branch.setDueDayValue(request.getDueDayValue());
        return toResponse(branchRepository.save(branch));
    }

    public void deleteBranch(Long id) {
        if (!branchRepository.existsById(id)) {
            throw new RuntimeException("Branch not found with id: " + id);
        }
        branchRepository.deleteById(id);
    }

    private BranchResponse toResponse(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .name(branch.getName())
                .duration(branch.getDuration())
                .schedule(branch.getSchedule())
                .color(branch.getColor())
                .totalFee(branch.getTotalFee())
                .installmentsCount(branch.getInstallmentsCount())
                .dueDayValue(branch.getDueDayValue())
                .build();
    }
}
