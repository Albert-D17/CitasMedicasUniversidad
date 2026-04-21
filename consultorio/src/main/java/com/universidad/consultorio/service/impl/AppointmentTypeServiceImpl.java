package com.universidad.consultorio.service.impl;

import com.universidad.consultorio.dto.request.CreateAppointmentTypeRequest;
import com.universidad.consultorio.dto.response.AppointmentTypeResponse;
import com.universidad.consultorio.entity.AppointmentType;
import com.universidad.consultorio.mapper.AppointmentTypeMapper;
import com.universidad.consultorio.repository.AppointmentTypeRepository;
import com.universidad.consultorio.service.AppointmentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentTypeServiceImpl implements AppointmentTypeService {

    private final AppointmentTypeRepository appointmentTypeRepository;
    private final AppointmentTypeMapper appointmentTypeMapper;

    @Override
    @Transactional
    public AppointmentTypeResponse create(CreateAppointmentTypeRequest request) {
        AppointmentType at = AppointmentType.builder()
                .name(request.getName())
                .durationMinutes(request.getDurationMinutes())
                .description(request.getDescription())
                .build();
        return appointmentTypeMapper.toResponse(appointmentTypeRepository.save(at));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentTypeResponse> findAll() {
        return appointmentTypeRepository.findAll().stream().map(appointmentTypeMapper::toResponse).toList();
    }
}
