package com.universidad.consultorio.service.impl;

import com.universidad.consultorio.dto.request.CreateDoctorRequest;
import com.universidad.consultorio.dto.request.UpdateDoctorRequest;
import com.universidad.consultorio.dto.response.DoctorResponse;
import com.universidad.consultorio.entity.Doctor;
import com.universidad.consultorio.entity.Specialty;
import com.universidad.consultorio.exception.ResourceNotFoundException;
import com.universidad.consultorio.mapper.DoctorMapper;
import com.universidad.consultorio.repository.DoctorRepository;
import com.universidad.consultorio.repository.SpecialtyRepository;
import com.universidad.consultorio.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final DoctorMapper doctorMapper;

    @Override
    @Transactional
    public DoctorResponse create(CreateDoctorRequest request) {
        Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found: " + request.getSpecialtyId()));

        Doctor doctor = Doctor.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .licenseNumber(request.getLicenseNumber())
                .email(request.getEmail())
                .specialty(specialty)
                .build();

        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse findById(Long id) {
        return doctorMapper.toResponse(getOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> findAll() {
        return doctorRepository.findAll().stream().map(doctorMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> findBySpecialty(Long specialtyId) {
        return doctorRepository.findBySpecialtyIdAndActiveTrue(specialtyId)
                .stream().map(doctorMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public DoctorResponse update(Long id, UpdateDoctorRequest request) {
        Doctor doctor = getOrThrow(id);

        if (request.getFirstName()   != null) doctor.setFirstName(request.getFirstName());
        if (request.getLastName()    != null) doctor.setLastName(request.getLastName());
        if (request.getEmail()       != null) doctor.setEmail(request.getEmail());
        if (request.getActive()      != null) doctor.setActive(request.getActive());
        if (request.getSpecialtyId() != null) {
            Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialty not found: " + request.getSpecialtyId()));
            doctor.setSpecialty(specialty);
        }

        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    private Doctor getOrThrow(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }
}
