package com.universidad.consultorio.mapper;

import com.universidad.consultorio.dto.response.DoctorResponse;
import com.universidad.consultorio.entity.Doctor;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {
    public DoctorResponse toResponse(Doctor d) {
        return DoctorResponse.builder()
                .id(d.getId())
                .firstName(d.getFirstName())
                .lastName(d.getLastName())
                .licenseNumber(d.getLicenseNumber())
                .email(d.getEmail())
                .active(d.isActive())
                .specialtyId(d.getSpecialty().getId())
                .specialtyName(d.getSpecialty().getName())
                .build();
    }
}
