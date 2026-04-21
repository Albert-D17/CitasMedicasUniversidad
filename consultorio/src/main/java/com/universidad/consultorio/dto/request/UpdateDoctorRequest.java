package com.universidad.consultorio.dto.request;

import lombok.Data;

@Data
public class UpdateDoctorRequest {
    private String firstName;
    private String lastName;
    private String email;
    private Long specialtyId;
    private Boolean active;
}
