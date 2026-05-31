package com.universidad.consultorio.dto.Response;

import lombok.Builder;

@Builder
public record NoShowPatientResponse(
        Long patientId,
        String patientName,
        String documentNumber,
        Long noShowCount
) {}
