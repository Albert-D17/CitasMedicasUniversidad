package com.universidad.consultorio.dto.Response;

import com.universidad.consultorio.enums.AppointmentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

// ==========================================
// 1. APPOINTMENT RESPONSE
// ==========================================
@Builder
public record AppointmentResponse(
        Long id,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        Long officeId,
        String officeName,
        Long appointmentTypeId,
        String appointmentTypeName,
        LocalDateTime startAt,
        LocalDateTime endAt,
        AppointmentStatus status,
        String cancellationReason,
        String observations,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
