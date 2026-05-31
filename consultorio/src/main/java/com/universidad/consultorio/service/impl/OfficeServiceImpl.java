
package com.universidad.consultorio.service.impl;

import com.universidad.consultorio.dto.Request.CreateOfficeRequest;
import com.universidad.consultorio.dto.Request.UpdateOfficeRequest;
import com.universidad.consultorio.dto.Response.OfficeResponse;
import com.universidad.consultorio.entity.Office;
import com.universidad.consultorio.exception.ResourceNotFoundException;
import com.universidad.consultorio.mapper.OfficeMapper;
import com.universidad.consultorio.repository.OfficeRepository;
import com.universidad.consultorio.service.OfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                .name(request.name())
                .location(request.location())
                .floor(request.floor())
                .status(request.status())
                .build();
        return officeMapper.toResponse(officeRepository.save(office));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OfficeResponse> findAll(Pageable pageable) {
        return officeRepository.findAll(pageable)
                .map(officeMapper::toResponse);
    }
    @Override
    @Transactional
    public OfficeResponse update(Long id, UpdateOfficeRequest request) {
        Office office = officeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Office not found with id: " + id));

        if (request.name().isPresent()) {
            office.setName(request.name().get());
        }
        if (request.location().isPresent()) {
            office.setLocation(request.location().get());
        }
        if (request.floor().isPresent()) {
            office.setFloor(request.floor().get());
        }
        if (request.status().isPresent()) {
            office.setStatus(request.status().get());
        }

        return officeMapper.toResponse(officeRepository.save(office));
    }
}