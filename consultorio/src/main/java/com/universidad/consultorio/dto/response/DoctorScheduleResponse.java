package com.universidad.consultorio.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data @Builder
public class DoctorScheduleResponse {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
