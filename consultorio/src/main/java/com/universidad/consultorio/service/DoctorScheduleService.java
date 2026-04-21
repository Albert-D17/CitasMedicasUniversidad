package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.request.CreateDoctorScheduleRequest;
import com.universidad.consultorio.dto.response.DoctorScheduleResponse;

import java.util.List;

public interface DoctorScheduleService {
    DoctorScheduleResponse create(Long doctorId, CreateDoctorScheduleRequest request);
    List<DoctorScheduleResponse> findByDoctor(Long doctorId);
}
