package com.universidad.consultorio.dto.response;

import com.universidad.consultorio.enums.AppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class AppointmentResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private Long officeId;
    private String officeName;
    private Long appointmentTypeId;
    private String appointmentTypeName;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private AppointmentStatus status;
    private String cancellationReason;
    private String observations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
