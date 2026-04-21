package com.universidad.consultorio.dto.response;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class DoctorResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String licenseNumber;
    private String email;
    private boolean active;
    private Long specialtyId;
    private String specialtyName;
}
