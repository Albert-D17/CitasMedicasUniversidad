package com.universidad.consultorio.dto.Response;

import lombok.Builder;

@Builder
public record OfficeOccupancyResponse(
        Long officeId,
        String officeName,
        Long appointmentCount
) {}
