package com.beeline.sms.service;

import com.beeline.sms.dto.StaffRegistrationRequest;
import com.beeline.sms.dto.StaffResponse;
import com.beeline.sms.entity.Branch;
import com.beeline.sms.entity.Staff;
import com.beeline.sms.entity.User;
import com.beeline.sms.enums.Role;
import com.beeline.sms.enums.UserStatus;
import com.beeline.sms.repository.BranchRepository;
import com.beeline.sms.repository.StaffRepository;
import com.beeline.sms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    public List<StaffResponse> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public StaffResponse registerStaff(StaffRegistrationRequest request) {
        List<Branch> branches = branchRepository.findAllById(request.getBranchIds());
        if (branches.size() != request.getBranchIds().size()) {
            throw new RuntimeException("One or more branch IDs are invalid");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .role(Role.valueOf(request.getRole().toUpperCase()))
                .status(UserStatus.ACTIVE)
                .build();
        user = userRepository.save(user);

        Staff staff = Staff.builder()
                .user(user)
                .branches(branches)
                .canMarkAttendance(request.getCanMarkAttendance() != null ? request.getCanMarkAttendance() : false)
                .build();
        staff = staffRepository.save(staff);

        return toResponse(staff);
    }

    @Transactional
    public StaffResponse updateStaff(Long id, StaffRegistrationRequest request) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id));

        List<Branch> branches = branchRepository.findAllById(request.getBranchIds());
        if (branches.size() != request.getBranchIds().size()) {
            throw new RuntimeException("One or more branch IDs are invalid");
        }

        User user = staff.getUser();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        userRepository.save(user);

        staff.setBranches(branches);
        if (request.getCanMarkAttendance() != null) {
            staff.setCanMarkAttendance(request.getCanMarkAttendance());
        }
        staffRepository.save(staff);

        return toResponse(staff);
    }

    @Transactional
    public void deleteStaff(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id));
        User user = staff.getUser();
        staff.getBranches().clear();
        staffRepository.delete(staff);
        userRepository.delete(user);
    }

    @Transactional
    public StaffResponse toggleAttendancePermission(Long staffId, Boolean canMark) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + staffId));
        staff.setCanMarkAttendance(canMark != null ? canMark : !staff.getCanMarkAttendance());
        staffRepository.save(staff);
        return toResponse(staff);
    }

    private StaffResponse toResponse(Staff staff) {
        User user = staff.getUser();
        List<StaffResponse.BranchInfo> branchInfos = staff.getBranches().stream()
                .map(b -> StaffResponse.BranchInfo.builder()
                        .id(b.getId())
                        .name(b.getName())
                        .build())
                .toList();

        return StaffResponse.builder()
                .id(staff.getId())
                .userId(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .canMarkAttendance(staff.getCanMarkAttendance())
                .branches(branchInfos)
                .build();
    }
}
