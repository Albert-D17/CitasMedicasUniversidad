package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.Request.CreateDoctorRequest;
import com.universidad.consultorio.dto.Request.UpdateDoctorRequest;
import com.universidad.consultorio.dto.Response.DoctorResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DoctorService {
    DoctorResponse create(CreateDoctorRequest request);
    DoctorResponse findById(Long id);
    List<DoctorResponse> findAll(Pageable page);
    List<DoctorResponse> findBySpecialty(Long specialtyId, Pageable pageable);
    DoctorResponse update(Long id, UpdateDoctorRequest request);
}
