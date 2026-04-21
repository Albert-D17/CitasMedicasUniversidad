package com.universidad.consultorio.dto.response;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class NoShowPatientResponse {
    private Long patientId;
    private String patientName;
    private String documentNumber;
    private Long noShowCount;
}
