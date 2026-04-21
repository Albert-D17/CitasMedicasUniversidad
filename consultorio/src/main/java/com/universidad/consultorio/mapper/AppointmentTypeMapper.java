package com.universidad.consultorio.mapper;

import com.universidad.consultorio.dto.response.AppointmentTypeResponse;
import com.universidad.consultorio.entity.AppointmentType;
import org.springframework.stereotype.Component;

@Component
public class AppointmentTypeMapper {
    public AppointmentTypeResponse toResponse(AppointmentType at) {
        return AppointmentTypeResponse.builder()
                .id(at.getId())
                .name(at.getName())
                .durationMinutes(at.getDurationMinutes())
                .description(at.getDescription())
                .build();
    }
}
