package com.universidad.consultorio.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePatientRequest {
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @NotBlank(message = "Document number is required")
    private String documentNumber;
    @Email(message = "Invalid email")
    private String email;
    private String phone;
}
