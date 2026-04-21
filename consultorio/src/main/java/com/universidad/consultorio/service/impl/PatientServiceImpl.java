package com.universidad.consultorio.service.impl;

import com.universidad.consultorio.dto.request.CreatePatientRequest;
import com.universidad.consultorio.dto.request.UpdatePatientRequest;
import com.universidad.consultorio.dto.response.PatientResponse;
import com.universidad.consultorio.entity.Patient;
import com.universidad.consultorio.exception.ConflictException;
import com.universidad.consultorio.exception.ResourceNotFoundException;
import com.universidad.consultorio.mapper.PatientMapper;
import com.universidad.consultorio.repository.PatientRepository;
import com.universidad.consultorio.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    @Transactional
    public PatientResponse create(CreatePatientRequest request) {
        patientRepository.findByDocumentNumber(request.getDocumentNumber())
                .ifPresent(p -> { throw new ConflictException(
                        "Patient with document number " + request.getDocumentNumber() + " already exists"); });

        Patient patient = Patient.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .documentNumber(request.getDocumentNumber())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();

        return patientMapper.toResponse(patientRepository.save(patient));
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse findById(Long id) {
        return patientMapper.toResponse(getOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> findAll() {
        return patientRepository.findAll().stream().map(patientMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public PatientResponse update(Long id, UpdatePatientRequest request) {
        Patient patient = getOrThrow(id);

        if (request.getFirstName() != null) patient.setFirstName(request.getFirstName());
        if (request.getLastName()  != null) patient.setLastName(request.getLastName());
        if (request.getEmail()     != null) patient.setEmail(request.getEmail());
        if (request.getPhone()     != null) patient.setPhone(request.getPhone());
        if (request.getStatus()    != null) patient.setStatus(request.getStatus());

        return patientMapper.toResponse(patientRepository.save(patient));
    }

    private Patient getOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }
}
