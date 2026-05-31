
package com.universidad.consultorio.service.impl;

import com.universidad.consultorio.dto.Request.CreateDoctorRequest;
import com.universidad.consultorio.dto.Request.UpdateDoctorRequest;
import com.universidad.consultorio.dto.Response.DoctorResponse;
import com.universidad.consultorio.entity.Doctor;
import com.universidad.consultorio.entity.Specialty;
import com.universidad.consultorio.exception.ResourceNotFoundException;
import com.universidad.consultorio.mapper.DoctorMapper;
import com.universidad.consultorio.repository.DoctorRepository;
import com.universidad.consultorio.repository.SpecialtyRepository;
import com.universidad.consultorio.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
        Specialty specialty = specialtyRepository.findById(request.specialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found: " + request.specialtyId()));

        Doctor doctor = Doctor.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .licenseNumber(request.licenseNumber())
                .email(request.email())
                .specialty(specialty)
                .active(true)
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
    public List<DoctorResponse> findAll(Pageable pageable) {
        return doctorRepository.findAll(pageable).stream()
                .map(doctorMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> findBySpecialty(Long specialtyId, Pageable pageable) {
        return doctorRepository.findBySpecialtyIdAndActiveTrue(specialtyId, pageable).stream()
                .map(doctorMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DoctorResponse update(Long id, UpdateDoctorRequest request) {
        Doctor doctor = getOrThrow(id);

        // ✅ CORRECTO: Usar JsonNullable.isPresent()
        if (request.firstName().isPresent()) {
            doctor.setFirstName(request.firstName().get());
        }
        if (request.lastName().isPresent()) {
            doctor.setLastName(request.lastName().get());
        }
        if (request.email().isPresent()) {
            doctor.setEmail(request.email().get());
        }
        if (request.active().isPresent()) {
            doctor.setActive(request.active().get());
        }
        if (request.specialtyId().isPresent()) {
            Specialty specialty = specialtyRepository.findById(request.specialtyId().get())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialty not found: " + request.specialtyId().get()));
            doctor.setSpecialty(specialty);
        }

        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    private Doctor getOrThrow(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }
}