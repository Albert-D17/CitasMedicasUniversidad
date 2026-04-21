package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.request.CancelAppointmentRequest;
import com.universidad.consultorio.dto.request.CreateAppointmentRequest;
import com.universidad.consultorio.dto.response.AppointmentResponse;

import java.util.List;

public interface AppointmentService {
    AppointmentResponse create(CreateAppointmentRequest request);
    AppointmentResponse findById(Long id);
    List<AppointmentResponse> findAll();
    AppointmentResponse confirm(Long id);
    AppointmentResponse cancel(Long id, CancelAppointmentRequest request);
    AppointmentResponse complete(Long id, String observations);
    AppointmentResponse markNoShow(Long id);
}
