package com.universidad.consultorio.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAppointmentRequest {
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    @NotNull(message = "Office ID is required")
    private Long officeId;
    @NotNull(message = "Appointment type ID is required")
    private Long appointmentTypeId;
    @NotNull(message = "Start date/time is required")
    private LocalDateTime startAt;
}
