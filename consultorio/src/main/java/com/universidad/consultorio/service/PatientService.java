package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.request.CreatePatientRequest;
import com.universidad.consultorio.dto.request.UpdatePatientRequest;
import com.universidad.consultorio.dto.response.PatientResponse;

import java.util.List;

public interface PatientService {
    PatientResponse create(CreatePatientRequest request);
    PatientResponse findById(Long id);
    List<PatientResponse> findAll();
    PatientResponse update(Long id, UpdatePatientRequest request);
}
