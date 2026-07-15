package com.beeline.sms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class HomeworkTaskRequest {

    @NotNull
    private Long branchId;

    @NotNull
    private LocalDate assignedDate;

    @NotEmpty
    private List<@NotBlank String> tasks;
}
