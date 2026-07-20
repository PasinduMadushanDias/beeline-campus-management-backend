package com.beeline.sms.service;

import com.beeline.sms.dto.AuthResponse;
import com.beeline.sms.dto.LoginRequest;
import com.beeline.sms.entity.Staff;
import com.beeline.sms.entity.Student;
import com.beeline.sms.entity.User;
import com.beeline.sms.enums.Role;
import com.beeline.sms.repository.StaffRepository;
import com.beeline.sms.repository.StudentRepository;
import com.beeline.sms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse authenticate(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        List<AuthResponse.BranchAssignment> branches = resolveBranches(user);
        Boolean canMarkAttendance = resolveCanMarkAttendance(user);

        return AuthResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .canMarkAttendance(canMarkAttendance)
                .branches(branches)
                .build();
    }

    private List<AuthResponse.BranchAssignment> resolveBranches(User user) {
        if (user.getRole() == Role.STUDENT) {
            return studentRepository.findByUserId(user.getId())
                    .map(Student::getBranch)
                    .map(b -> List.of(AuthResponse.BranchAssignment.builder()
                            .id(b.getId())
                            .name(b.getName())
                            .build()))
                    .orElse(Collections.emptyList());
        }

        if (user.getRole() == Role.STAFF || user.getRole() == Role.TEACHER) {
            return staffRepository.findByUserId(user.getId())
                    .map(Staff::getBranches)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(b -> AuthResponse.BranchAssignment.builder()
                            .id(b.getId())
                            .name(b.getName())
                            .build())
                    .toList();
        }

        return Collections.emptyList();
    }

    private Boolean resolveCanMarkAttendance(User user) {
        if (user.getRole() == Role.STAFF || user.getRole() == Role.TEACHER) {
            return staffRepository.findByUserId(user.getId())
                    .map(Staff::getCanMarkAttendance)
                    .orElse(false);
        }
        return null;
    }
}
