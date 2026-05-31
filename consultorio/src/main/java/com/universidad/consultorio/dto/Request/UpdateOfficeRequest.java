package com.universidad.consultorio.dto.Request;

import com.universidad.consultorio.enums.OfficeStatus;
import org.openapitools.jackson.nullable.JsonNullable;

public record UpdateOfficeRequest(
        JsonNullable<String> name,
        JsonNullable<String> location,
        JsonNullable<String> floor,
        JsonNullable<OfficeStatus> status
) {}
