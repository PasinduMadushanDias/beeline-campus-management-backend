package com.beeline.sms.dto;

import com.beeline.sms.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRegistrationRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private String email;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Telephone number is required")
    private String telephone;

    @NotNull(message = "Birthday is required")
    private LocalDate birthday;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "NIC is required")
    private String nic;
}