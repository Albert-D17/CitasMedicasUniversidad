package com.universidad.consultorio.service.impl;

import com.universidad.consultorio.dto.request.CreateSpecialtyRequest;
import com.universidad.consultorio.dto.response.SpecialtyResponse;
import com.universidad.consultorio.entity.Specialty;
import com.universidad.consultorio.exception.ConflictException;
import com.universidad.consultorio.exception.ResourceNotFoundException;
import com.universidad.consultorio.mapper.SpecialtyMapper;
import com.universidad.consultorio.repository.SpecialtyRepository;
import com.universidad.consultorio.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    @Override
    @Transactional
    public SpecialtyResponse create(CreateSpecialtyRequest request) {
        specialtyRepository.findByNameIgnoreCase(request.getName())
                .ifPresent(s -> { throw new ConflictException("Specialty already exists: " + request.getName()); });

        Specialty specialty = Specialty.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return specialtyMapper.toResponse(specialtyRepository.save(specialty));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponse> findAll() {
        return specialtyRepository.findAll().stream().map(specialtyMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyResponse findById(Long id) {
        return specialtyMapper.toResponse(specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id)));
    }
}
