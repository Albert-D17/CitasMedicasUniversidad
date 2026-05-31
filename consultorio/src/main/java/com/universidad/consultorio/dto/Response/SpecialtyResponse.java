package com.universidad.consultorio.dto.Response;

import lombok.Builder;

@Builder
public record SpecialtyResponse(
            Long id,
            String name,
            String description
    ) {}

