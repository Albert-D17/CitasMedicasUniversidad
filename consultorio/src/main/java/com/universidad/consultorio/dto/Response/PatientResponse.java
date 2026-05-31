package com.universidad.consultorio.dto.Response;

import com.universidad.consultorio.enums.PatientStatus;
import lombok.Builder;

import java.time.LocalDateTime;

// ==========================================
// 6. PATIENT RESPONSE
// ==========================================
@Builder
public record PatientResponse(
        Long id,
        String firstName,
        String lastName,
        String documentNumber,
        String email,
        String phone,
        PatientStatus status,
        LocalDateTime createdAt
) {}
