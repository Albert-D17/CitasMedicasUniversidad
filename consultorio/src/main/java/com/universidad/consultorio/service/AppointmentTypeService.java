package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.Request.CreateAppointmentTypeRequest;
import com.universidad.consultorio.dto.Response.AppointmentTypeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface AppointmentTypeService {
    AppointmentTypeResponse create(CreateAppointmentTypeRequest request);
    Page<AppointmentTypeResponse> findAll(Pageable page);
}
