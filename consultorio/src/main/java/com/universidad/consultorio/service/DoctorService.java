package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.request.CreateDoctorRequest;
import com.universidad.consultorio.dto.request.UpdateDoctorRequest;
import com.universidad.consultorio.dto.response.DoctorResponse;

import java.util.List;

public interface DoctorService {
    DoctorResponse create(CreateDoctorRequest request);
    DoctorResponse findById(Long id);
    List<DoctorResponse> findAll();
    List<DoctorResponse> findBySpecialty(Long specialtyId);
    DoctorResponse update(Long id, UpdateDoctorRequest request);
}
