package com.universidad.consultorio.mapper;

import com.universidad.consultorio.dto.response.SpecialtyResponse;
import com.universidad.consultorio.entity.Specialty;
import org.springframework.stereotype.Component;

@Component
public class SpecialtyMapper {
    public SpecialtyResponse toResponse(Specialty s) {
        return SpecialtyResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .build();
    }
}
