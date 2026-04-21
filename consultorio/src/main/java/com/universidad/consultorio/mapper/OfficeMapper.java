package com.universidad.consultorio.mapper;

import com.universidad.consultorio.dto.response.OfficeResponse;
import com.universidad.consultorio.entity.Office;
import org.springframework.stereotype.Component;

@Component
public class OfficeMapper {
    public OfficeResponse toResponse(Office o) {
        return OfficeResponse.builder()
                .id(o.getId())
                .name(o.getName())
                .location(o.getLocation())
                .floor(o.getFloor())
                .status(o.getStatus())
                .build();
    }
}
