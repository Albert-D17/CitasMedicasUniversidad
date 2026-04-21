package com.universidad.consultorio.dto.response;

import com.universidad.consultorio.enums.PatientStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class PatientResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String documentNumber;
    private String email;
    private String phone;
    private PatientStatus status;
    private LocalDateTime createdAt;
}
