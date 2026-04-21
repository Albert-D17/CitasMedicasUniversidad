package com.universidad.consultorio.service;

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
import com.universidad.consultorio.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private OfficeRepository officeRepository;
    @Mock private AppointmentTypeRepository appointmentTypeRepository;
    @Mock private DoctorScheduleRepository doctorScheduleRepository;
    @Mock private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Patient activePatient;
    private Doctor activeDoctor;
    private Office activeOffice;
    private AppointmentType appointmentType;
    private DoctorSchedule mondaySchedule;
    private LocalDateTime nextMonday9am;

    @BeforeEach
    void setUp() {
        Specialty specialty = Specialty.builder().id(1L).name("Medicina").build();

        activePatient = Patient.builder().id(1L).firstName("Ana").lastName("López")
                .documentNumber("123").status(PatientStatus.ACTIVE).build();

        activeDoctor = Doctor.builder().id(1L).firstName("Dr").lastName("García")
                .licenseNumber("LIC-1").specialty(specialty).active(true).build();

        activeOffice = Office.builder().id(1L).name("C-01").status(OfficeStatus.ACTIVE).build();

        appointmentType = AppointmentType.builder().id(1L)
                .name("Consulta").durationMinutes(30).build();

        // próximo lunes a las 09:00
        nextMonday9am = LocalDateTime.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .withHour(9).withMinute(0).withSecond(0).withNano(0);

        mondaySchedule = DoctorSchedule.builder()
                .id(1L).doctor(activeDoctor).dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(8, 0)).endTime(LocalTime.of(17, 0)).build();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private CreateAppointmentRequest buildRequest() {
        CreateAppointmentRequest req = new CreateAppointmentRequest();
        req.setPatientId(1L);
        req.setDoctorId(1L);
        req.setOfficeId(1L);
        req.setAppointmentTypeId(1L);
        req.setStartAt(nextMonday9am);
        return req;
    }

    private void mockHappyPath() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(activePatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(activeDoctor));
        when(officeRepository.findById(1L)).thenReturn(Optional.of(activeOffice));
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.of(appointmentType));
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(1L, DayOfWeek.MONDAY))
                .thenReturn(List.of(mondaySchedule));
        when(appointmentRepository.existsDoctorOverlap(anyLong(), any(), any(), any())).thenReturn(false);
        when(appointmentRepository.existsOfficeOverlap(anyLong(), any(), any(), any())).thenReturn(false);
        when(appointmentRepository.existsPatientOverlap(anyLong(), any(), any(), any())).thenReturn(false);
    }

    // ── create ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("create - happy path saves appointment with SCHEDULED status and computed endAt")
    void create_success() {
        mockHappyPath();
        Appointment saved = Appointment.builder().id(10L)
                .patient(activePatient).doctor(activeDoctor).office(activeOffice)
                .appointmentType(appointmentType)
                .startAt(nextMonday9am).endAt(nextMonday9am.plusMinutes(30))
                .status(AppointmentStatus.SCHEDULED).build();
        when(appointmentRepository.save(any())).thenReturn(saved);
        AppointmentResponse response = AppointmentResponse.builder().id(10L)
                .status(AppointmentStatus.SCHEDULED).build();
        when(appointmentMapper.toResponse(saved)).thenReturn(response);

        AppointmentResponse result = appointmentService.create(buildRequest());

        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        verify(appointmentRepository).save(argThat(a ->
                a.getEndAt().equals(nextMonday9am.plusMinutes(30)) &&
                a.getStatus() == AppointmentStatus.SCHEDULED
        ));
    }

    @Test
    @DisplayName("create - throws BusinessException when patient is inactive")
    void create_inactivePatient() {
        activePatient.setStatus(PatientStatus.INACTIVE);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(activePatient));

        assertThatThrownBy(() -> appointmentService.create(buildRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("inactive");
    }

    @Test
    @DisplayName("create - throws BusinessException when doctor is inactive")
    void create_inactiveDoctor() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(activePatient));
        activeDoctor.setActive(false);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(activeDoctor));

        assertThatThrownBy(() -> appointmentService.create(buildRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("inactive");
    }

    @Test
    @DisplayName("create - throws BusinessException when appointment is in the past")
    void create_pastDate() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(activePatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(activeDoctor));
        when(officeRepository.findById(1L)).thenReturn(Optional.of(activeOffice));
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.of(appointmentType));

        CreateAppointmentRequest req = buildRequest();
        req.setStartAt(LocalDateTime.now().minusDays(1));

        assertThatThrownBy(() -> appointmentService.create(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("past");
    }

    @Test
    @DisplayName("create - throws BusinessException when outside doctor working hours")
    void create_outsideSchedule() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(activePatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(activeDoctor));
        when(officeRepository.findById(1L)).thenReturn(Optional.of(activeOffice));
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.of(appointmentType));
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(1L, DayOfWeek.MONDAY))
                .thenReturn(List.of(mondaySchedule));

        CreateAppointmentRequest req = buildRequest();
        // Fuera del horario: 20:00
        req.setStartAt(nextMonday9am.withHour(20));

        assertThatThrownBy(() -> appointmentService.create(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("working hours");
    }

    @Test
    @DisplayName("create - throws ConflictException when doctor has overlap")
    void create_doctorOverlap() {
        mockHappyPath();
        when(appointmentRepository.existsDoctorOverlap(anyLong(), any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> appointmentService.create(buildRequest()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Doctor already has");
    }

    @Test
    @DisplayName("create - throws ConflictException when office has overlap")
    void create_officeOverlap() {
        mockHappyPath();
        when(appointmentRepository.existsOfficeOverlap(anyLong(), any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> appointmentService.create(buildRequest()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Office is already occupied");
    }

    // ── confirm ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("confirm - SCHEDULED transitions to CONFIRMED")
    void confirm_success() {
        Appointment appt = Appointment.builder().id(1L).status(AppointmentStatus.SCHEDULED)
                .patient(activePatient).doctor(activeDoctor).office(activeOffice)
                .appointmentType(appointmentType)
                .startAt(nextMonday9am).endAt(nextMonday9am.plusMinutes(30)).build();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appt));
        when(appointmentRepository.save(any())).thenReturn(appt);
        when(appointmentMapper.toResponse(any())).thenReturn(
                AppointmentResponse.builder().id(1L).status(AppointmentStatus.CONFIRMED).build());

        AppointmentResponse result = appointmentService.confirm(1L);
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
    }

    @Test
    @DisplayName("confirm - throws BusinessException when appointment is CANCELLED")
    void confirm_cancelled() {
        Appointment appt = Appointment.builder().id(1L).status(AppointmentStatus.CANCELLED).build();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appt));

        assertThatThrownBy(() -> appointmentService.confirm(1L))
                .isInstanceOf(BusinessException.class);
    }

    // ── cancel ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("cancel - CONFIRMED transitions to CANCELLED with reason")
    void cancel_success() {
        Appointment appt = Appointment.builder().id(1L).status(AppointmentStatus.CONFIRMED)
                .patient(activePatient).doctor(activeDoctor).office(activeOffice)
                .appointmentType(appointmentType)
                .startAt(nextMonday9am).endAt(nextMonday9am.plusMinutes(30)).build();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appt));
        when(appointmentRepository.save(any())).thenReturn(appt);
        when(appointmentMapper.toResponse(any())).thenReturn(
                AppointmentResponse.builder().id(1L).status(AppointmentStatus.CANCELLED).build());

        CancelAppointmentRequest req = new CancelAppointmentRequest();
        req.setReason("Patient request");

        AppointmentResponse result = appointmentService.cancel(1L, req);
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
        verify(appointmentRepository).save(argThat(a -> "Patient request".equals(a.getCancellationReason())));
    }

    @Test
    @DisplayName("cancel - throws BusinessException when appointment is COMPLETED")
    void cancel_completed() {
        Appointment appt = Appointment.builder().id(1L).status(AppointmentStatus.COMPLETED).build();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appt));

        CancelAppointmentRequest req = new CancelAppointmentRequest();
        req.setReason("reason");

        assertThatThrownBy(() -> appointmentService.cancel(1L, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("COMPLETED");
    }

    // ── complete ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("complete - CONFIRMED transitions to COMPLETED after start time")
    void complete_success() {
        Appointment appt = Appointment.builder().id(1L).status(AppointmentStatus.CONFIRMED)
                .patient(activePatient).doctor(activeDoctor).office(activeOffice)
                .appointmentType(appointmentType)
                .startAt(LocalDateTime.now().minusHours(1))
                .endAt(LocalDateTime.now().minusMinutes(30)).build();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appt));
        when(appointmentRepository.save(any())).thenReturn(appt);
        when(appointmentMapper.toResponse(any())).thenReturn(
                AppointmentResponse.builder().id(1L).status(AppointmentStatus.COMPLETED).build());

        AppointmentResponse result = appointmentService.complete(1L, "All good");
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    @DisplayName("complete - throws BusinessException when appointment has not started yet")
    void complete_beforeStart() {
        Appointment appt = Appointment.builder().id(1L).status(AppointmentStatus.CONFIRMED)
                .startAt(LocalDateTime.now().plusHours(2)).build();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appt));

        assertThatThrownBy(() -> appointmentService.complete(1L, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("before its scheduled start");
    }

    // ── no-show ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("markNoShow - CONFIRMED transitions to NO_SHOW after start time")
    void markNoShow_success() {
        Appointment appt = Appointment.builder().id(1L).status(AppointmentStatus.CONFIRMED)
                .patient(activePatient).doctor(activeDoctor).office(activeOffice)
                .appointmentType(appointmentType)
                .startAt(LocalDateTime.now().minusMinutes(10))
                .endAt(LocalDateTime.now().plusMinutes(20)).build();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appt));
        when(appointmentRepository.save(any())).thenReturn(appt);
        when(appointmentMapper.toResponse(any())).thenReturn(
                AppointmentResponse.builder().id(1L).status(AppointmentStatus.NO_SHOW).build());

        AppointmentResponse result = appointmentService.markNoShow(1L);
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.NO_SHOW);
    }

    @Test
    @DisplayName("markNoShow - throws BusinessException before start time")
    void markNoShow_beforeStart() {
        Appointment appt = Appointment.builder().id(1L).status(AppointmentStatus.CONFIRMED)
                .startAt(LocalDateTime.now().plusHours(1)).build();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appt));

        assertThatThrownBy(() -> appointmentService.markNoShow(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("before its scheduled start");
    }

    @Test
    @DisplayName("findById - throws ResourceNotFoundException when not found")
    void findById_notFound() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> appointmentService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
