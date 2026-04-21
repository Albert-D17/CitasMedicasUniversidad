package com.universidad.consultorio.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelAppointmentRequest {
    @NotBlank(message = "Cancellation reason is required")
    private String reason;
}
