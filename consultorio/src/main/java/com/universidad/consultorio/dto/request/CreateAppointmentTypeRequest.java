package com.universidad.consultorio.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAppointmentTypeRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotNull(message = "Duration in minutes is required")
    @Min(value = 5, message = "Duration must be at least 5 minutes")
    private Integer durationMinutes;
    private String description;
}
