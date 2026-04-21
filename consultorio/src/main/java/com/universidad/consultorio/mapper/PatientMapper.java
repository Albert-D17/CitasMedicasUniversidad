package com.universidad.consultorio.mapper;

import com.universidad.consultorio.dto.response.PatientResponse;
import com.universidad.consultorio.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {
    public PatientResponse toResponse(Patient p) {
        return PatientResponse.builder()
                .id(p.getId())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .documentNumber(p.getDocumentNumber())
                .email(p.getEmail())
                .phone(p.getPhone())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
