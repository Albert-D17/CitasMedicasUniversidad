package com.universidad.consultorio.dto.request;

import com.universidad.consultorio.enums.PatientStatus;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdatePatientRequest {
    private String firstName;
    private String lastName;
    @Email(message = "Invalid email")
    private String email;
    private String phone;
    private PatientStatus status;
}
