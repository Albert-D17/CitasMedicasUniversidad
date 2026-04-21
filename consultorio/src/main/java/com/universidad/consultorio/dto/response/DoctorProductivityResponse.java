package com.universidad.consultorio.dto.response;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class DoctorProductivityResponse {
    private Long doctorId;
    private String doctorName;
    private String specialtyName;
    private Long completedAppointments;
}
