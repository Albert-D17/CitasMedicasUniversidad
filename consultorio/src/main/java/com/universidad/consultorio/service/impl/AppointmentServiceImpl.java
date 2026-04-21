package com.universidad.consultorio.service.impl;

import com.universidad.consultorio.dto.request.CancelAppointmentRequest;
import com.universidad.consultorio.dto.request.CreateAppointmentRequest;
import com.universidad.consultorio.dto.response.AppointmentResponse;
import com.universidad.consultorio.entity.*;
import com.universidad.consultorio.enums.AppointmentStatus;
import com.universidad.consultorio.enums.OfficeStatus;
import com.universidad.consultorio.enums.PatientStatus;
import com.universidad.consultorio.exception.BusinessException;
import com.universidad.consultorio.exception.ConflictException;
import com.universidad.consultorio.exception.ResourceNotFoundException;
import com.universidad.consultorio.mapper.AppointmentMapper;
import com.universidad.consultorio.repository.*;
import com.universidad.consultorio.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final OfficeRepository officeRepository;
    private final AppointmentTypeRepository appointmentTypeRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    @Transactional
    public AppointmentResponse create(CreateAppointmentRequest request) {
        // 1. Validar que el paciente existe y está activo
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + request.getPatientId()));
        if (patient.getStatus() == PatientStatus.INACTIVE) {
            throw new BusinessException("Patient is inactive and cannot book appointments");
        }

        // 2. Validar que el doctor existe y está activo
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + request.getDoctorId()));
        if (!doctor.isActive()) {
            throw new BusinessException("Doctor is inactive and cannot receive appointments");
        }

        // 3. Validar que el consultorio existe y está activo
        Office office = officeRepository.findById(request.getOfficeId())
                .orElseThrow(() -> new ResourceNotFoundException("Office not found: " + request.getOfficeId()));
        if (office.getStatus() != OfficeStatus.ACTIVE) {
            throw new BusinessException("Office is not active");
        }

        // 4. Validar tipo de cita
        AppointmentType appointmentType = appointmentTypeRepository.findById(request.getAppointmentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment type not found: " + request.getAppointmentTypeId()));

        // 5. Validar que la fecha no sea en el pasado
        LocalDateTime startAt = request.getStartAt();
        if (startAt.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Cannot create an appointment in the past");
        }

        // 6. Calcular endAt (el cliente NO lo envía; el servicio lo construye)
        LocalDateTime endAt = startAt.plusMinutes(appointmentType.getDurationMinutes());

        // 7. Validar que la cita cae dentro del horario laboral del doctor
        DayOfWeek dayOfWeek = startAt.getDayOfWeek();
        List<DoctorSchedule> schedules = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(doctor.getId(), dayOfWeek);

        boolean withinSchedule = schedules.stream().anyMatch(s ->
                !startAt.toLocalTime().isBefore(s.getStartTime()) &&
                !endAt.toLocalTime().isAfter(s.getEndTime())
        );
        if (!withinSchedule) {
            throw new BusinessException("Appointment is outside the doctor's working hours for that day");
        }

        // 8. Validar traslape de doctor
        if (appointmentRepository.existsDoctorOverlap(doctor.getId(), startAt, endAt, null)) {
            throw new ConflictException("Doctor already has an appointment in that time range");
        }

        // 9. Validar traslape de consultorio
        if (appointmentRepository.existsOfficeOverlap(office.getId(), startAt, endAt, null)) {
            throw new ConflictException("Office is already occupied in that time range");
        }

        // 10. Validar traslape del paciente
        if (appointmentRepository.existsPatientOverlap(patient.getId(), startAt, endAt, null)) {
            throw new ConflictException("Patient already has an appointment in that time range");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .office(office)
                .appointmentType(appointmentType)
                .startAt(startAt)
                .endAt(endAt)
                .status(AppointmentStatus.SCHEDULED)
                .build();

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse findById(Long id) {
        return appointmentMapper.toResponse(getOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> findAll() {
        return appointmentRepository.findAll().stream().map(appointmentMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public AppointmentResponse confirm(Long id) {
        Appointment appointment = getOrThrow(id);

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BusinessException("Only SCHEDULED appointments can be confirmed. Current status: "
                    + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setUpdatedAt(LocalDateTime.now());
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse cancel(Long id, CancelAppointmentRequest request) {
        Appointment appointment = getOrThrow(id);

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessException("Cannot cancel a COMPLETED appointment");
        }
        if (appointment.getStatus() == AppointmentStatus.NO_SHOW) {
            throw new BusinessException("Cannot cancel a NO_SHOW appointment");
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessException("Appointment is already cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(request.getReason());
        appointment.setUpdatedAt(LocalDateTime.now());
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse complete(Long id, String observations) {
        Appointment appointment = getOrThrow(id);

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BusinessException("Only CONFIRMED appointments can be completed. Current status: "
                    + appointment.getStatus());
        }
        if (LocalDateTime.now().isBefore(appointment.getStartAt())) {
            throw new BusinessException("Cannot complete an appointment before its scheduled start time");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setObservations(observations);
        appointment.setUpdatedAt(LocalDateTime.now());
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse markNoShow(Long id) {
        Appointment appointment = getOrThrow(id);

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BusinessException("Only CONFIRMED appointments can be marked as NO_SHOW. Current status: "
                    + appointment.getStatus());
        }
        if (LocalDateTime.now().isBefore(appointment.getStartAt())) {
            throw new BusinessException("Cannot mark an appointment as NO_SHOW before its scheduled start time");
        }

        appointment.setStatus(AppointmentStatus.NO_SHOW);
        appointment.setUpdatedAt(LocalDateTime.now());
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    private Appointment getOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }
}
