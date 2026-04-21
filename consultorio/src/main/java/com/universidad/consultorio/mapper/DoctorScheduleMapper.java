package com.universidad.consultorio.mapper;

import com.universidad.consultorio.dto.response.DoctorScheduleResponse;
import com.universidad.consultorio.entity.DoctorSchedule;
import org.springframework.stereotype.Component;

@Component
public class DoctorScheduleMapper {
    public DoctorScheduleResponse toResponse(DoctorSchedule ds) {
        return DoctorScheduleResponse.builder()
                .id(ds.getId())
                .doctorId(ds.getDoctor().getId())
                .doctorName(ds.getDoctor().getFirstName() + " " + ds.getDoctor().getLastName())
                .dayOfWeek(ds.getDayOfWeek())
                .startTime(ds.getStartTime())
                .endTime(ds.getEndTime())
                .build();
    }
}
