package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.request.CreateAppointmentTypeRequest;
import com.universidad.consultorio.dto.response.AppointmentTypeResponse;

import java.util.List;

public interface AppointmentTypeService {
    AppointmentTypeResponse create(CreateAppointmentTypeRequest request);
    List<AppointmentTypeResponse> findAll();
}
