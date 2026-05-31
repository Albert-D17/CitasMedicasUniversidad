package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.Request.CreateAppointmentRequest;
import com.universidad.consultorio.dto.Response.AppointmentResponse;
import com.universidad.consultorio.dto.Request.CancelAppointmentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AppointmentService {
    AppointmentResponse create(CreateAppointmentRequest request);
    AppointmentResponse findById(Long id);
    Page<AppointmentResponse> findAll(Pageable Page);
    AppointmentResponse confirm(Long id);
    AppointmentResponse cancel(Long id, CancelAppointmentRequest request);
    AppointmentResponse complete(Long id, String observations);
    AppointmentResponse markNoShow(Long id);
}
