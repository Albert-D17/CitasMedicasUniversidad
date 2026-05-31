package com.universidad.consultorio.dto.Response;

import lombok.Builder;

@Builder
public record DoctorProductivityResponse(
        Long doctorId,
        String doctorName,
        String specialtyName,
        Long completedAppointments
) {}
