package com.universidad.consultorio.dto.response;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class AppointmentTypeResponse {
    private Long id;
    private String name;
    private Integer durationMinutes;
    private String description;
}
