package com.universidad.consultorio.service.impl;

import com.universidad.consultorio.dto.request.CreateDoctorScheduleRequest;
import com.universidad.consultorio.dto.response.DoctorScheduleResponse;
import com.universidad.consultorio.entity.Doctor;
import com.universidad.consultorio.entity.DoctorSchedule;
import com.universidad.consultorio.exception.BusinessException;
import com.universidad.consultorio.exception.ResourceNotFoundException;
import com.universidad.consultorio.mapper.DoctorScheduleMapper;
import com.universidad.consultorio.repository.DoctorRepository;
import com.universidad.consultorio.repository.DoctorScheduleRepository;
import com.universidad.consultorio.service.DoctorScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorScheduleMapper doctorScheduleMapper;

    @Override
    @Transactional
    public DoctorScheduleResponse create(Long doctorId, CreateDoctorScheduleRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        if (!doctor.isActive()) {
            throw new BusinessException("Cannot add schedule to an inactive doctor");
        }

        if (request.getStartTime().isAfter(request.getEndTime()) ||
            request.getStartTime().equals(request.getEndTime())) {
            throw new BusinessException("Start time must be before end time");
        }

        DoctorSchedule schedule = DoctorSchedule.builder()
                .doctor(doctor)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        return doctorScheduleMapper.toResponse(doctorScheduleRepository.save(schedule));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorScheduleResponse> findByDoctor(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + doctorId);
        }
        return doctorScheduleRepository.findByDoctorId(doctorId)
                .stream().map(doctorScheduleMapper::toResponse).toList();
    }
}
