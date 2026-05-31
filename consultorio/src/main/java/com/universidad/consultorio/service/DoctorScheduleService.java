package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.Request.CreateDoctorScheduleRequest;
import com.universidad.consultorio.dto.Response.DoctorScheduleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface DoctorScheduleService {
    DoctorScheduleResponse create(Long doctorId, CreateDoctorScheduleRequest request);
    Page<DoctorScheduleResponse> findByDoctor(Long doctorId, Pageable page);
}
