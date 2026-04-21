package com.universidad.consultorio.service.impl;

import com.universidad.consultorio.dto.request.CreateOfficeRequest;
import com.universidad.consultorio.dto.request.UpdateOfficeRequest;
import com.universidad.consultorio.dto.response.OfficeResponse;
import com.universidad.consultorio.entity.Office;
import com.universidad.consultorio.exception.ResourceNotFoundException;
import com.universidad.consultorio.mapper.OfficeMapper;
import com.universidad.consultorio.repository.OfficeRepository;
import com.universidad.consultorio.service.OfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfficeServiceImpl implements OfficeService {

    private final OfficeRepository officeRepository;
    private final OfficeMapper officeMapper;

    @Override
    @Transactional
    public OfficeResponse create(CreateOfficeRequest request) {
        Office office = Office.builder()
                .name(request.getName())
                .location(request.getLocation())
                .floor(request.getFloor())
                .build();
        return officeMapper.toResponse(officeRepository.save(office));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfficeResponse> findAll() {
        return officeRepository.findAll().stream().map(officeMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public OfficeResponse update(Long id, UpdateOfficeRequest request) {
        Office office = officeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Office not found with id: " + id));

        if (request.getName()     != null) office.setName(request.getName());
        if (request.getLocation() != null) office.setLocation(request.getLocation());
        if (request.getFloor()    != null) office.setFloor(request.getFloor());
        if (request.getStatus()   != null) office.setStatus(request.getStatus());

        return officeMapper.toResponse(officeRepository.save(office));
    }
}
