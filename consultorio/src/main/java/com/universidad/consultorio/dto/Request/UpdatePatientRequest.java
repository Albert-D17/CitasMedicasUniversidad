package com.universidad.consultorio.dto.Request;

import com.universidad.consultorio.enums.PatientStatus;
import org.openapitools.jackson.nullable.JsonNullable;

public record UpdatePatientRequest(
        JsonNullable<String> firstName,
        JsonNullable<String> lastName,
        JsonNullable<String> email,
        JsonNullable<String> phone,
        JsonNullable<PatientStatus> status
) {}
