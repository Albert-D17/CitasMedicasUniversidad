package com.universidad.consultorio.dto.Response;

import com.universidad.consultorio.enums.OfficeStatus;
import lombok.Builder;

// ==========================================
// 5. OFFICE RESPONSE
// ==========================================
@Builder
public record OfficeResponse(
        Long id,
        String name,
        String location,
        String floor,
        OfficeStatus status
) {}
