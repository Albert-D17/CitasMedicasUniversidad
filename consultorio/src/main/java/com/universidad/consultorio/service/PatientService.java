package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.Request.CreatePatientRequest;
import com.universidad.consultorio.dto.Request.UpdatePatientRequest;
import com.universidad.consultorio.dto.Response.PatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientService {
    PatientResponse create(CreatePatientRequest request);
    PatientResponse findById(Long id);
    Page<PatientResponse> findAll(Pageable page);
    PatientResponse update(Long id, UpdatePatientRequest request);
}
