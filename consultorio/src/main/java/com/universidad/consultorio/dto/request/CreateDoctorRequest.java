package com.universidad.consultorio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDoctorRequest {
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @NotBlank(message = "License number is required")
    private String licenseNumber;
    private String email;
    @NotNull(message = "Specialty ID is required")
    private Long specialtyId;
}
