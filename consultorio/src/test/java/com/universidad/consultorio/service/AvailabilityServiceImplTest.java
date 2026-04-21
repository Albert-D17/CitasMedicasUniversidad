package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.response.AvailabilitySlotResponse;
import com.universidad.consultorio.entity.*;
import com.universidad.consultorio.enums.AppointmentStatus;
import com.universidad.consultorio.exception.ResourceNotFoundException;
import com.universidad.consultorio.repository.*;
import com.universidad.consultorio.service.impl.AvailabilityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock private DoctorRepository doctorRepository;
    @Mock private DoctorScheduleRepository doctorScheduleRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private AppointmentTypeRepository appointmentTypeRepository;

    @InjectMocks
    private AvailabilityServiceImpl availabilityService;

    private LocalDate nextMonday;
    private DoctorSchedule schedule;
    private AppointmentType appointmentType30min;

    @BeforeEach
    void setUp() {
        nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        schedule = DoctorSchedule.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(11, 0))
                .build();
        appointmentType30min = AppointmentType.builder().id(1L).name("General").durationMinutes(30).build();
    }

    @Test
    @DisplayName("getAvailableSlots - returns 4 slots when no existing appointments")
    void getAvailableSlots_noExisting() {
        when(doctorRepository.existsById(1L)).thenReturn(true);
        when(appointmentTypeRepository.findById(1L)).thenReturn(java.util.Optional.of(appointmentType30min));
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(1L, DayOfWeek.MONDAY))
                .thenReturn(List.of(schedule));
        when(appointmentRepository.findActiveDoctorAppointmentsInRange(anyLong(), any(), any()))
                .thenReturn(List.of());

        List<AvailabilitySlotResponse> slots = availabilityService.getAvailableSlots(1L, nextMonday, 1L);

        // 09:00-09:30, 09:30-10:00, 10:00-10:30, 10:30-11:00
        assertThat(slots).hasSize(4);
        assertThat(slots.get(0).getStartAt().toLocalTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(slots.get(3).getStartAt().toLocalTime()).isEqualTo(LocalTime.of(10, 30));
    }

    @Test
    @DisplayName("getAvailableSlots - excludes slot occupied by existing appointment")
    void getAvailableSlots_withExistingAppointment() {
        when(doctorRepository.existsById(1L)).thenReturn(true);
        when(appointmentTypeRepository.findById(1L)).thenReturn(java.util.Optional.of(appointmentType30min));
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(1L, DayOfWeek.MONDAY))
                .thenReturn(List.of(schedule));

        // Cita existente ocupa 09:00-09:30
        LocalDateTime existingStart = nextMonday.atTime(9, 0);
        Appointment existing = Appointment.builder()
                .startAt(existingStart).endAt(existingStart.plusMinutes(30))
                .status(AppointmentStatus.CONFIRMED).build();
        when(appointmentRepository.findActiveDoctorAppointmentsInRange(anyLong(), any(), any()))
                .thenReturn(List.of(existing));

        List<AvailabilitySlotResponse> slots = availabilityService.getAvailableSlots(1L, nextMonday, 1L);

        assertThat(slots).hasSize(3);
        assertThat(slots.get(0).getStartAt().toLocalTime()).isEqualTo(LocalTime.of(9, 30));
    }

    @Test
    @DisplayName("getAvailableSlots - returns empty when doctor has no schedule on that day")
    void getAvailableSlots_noSchedule() {
        when(doctorRepository.existsById(1L)).thenReturn(true);
        when(appointmentTypeRepository.findById(1L)).thenReturn(java.util.Optional.of(appointmentType30min));
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(anyLong(), any()))
                .thenReturn(List.of());

        List<AvailabilitySlotResponse> slots = availabilityService.getAvailableSlots(1L, nextMonday, 1L);
        assertThat(slots).isEmpty();
    }

    @Test
    @DisplayName("getAvailableSlots - throws ResourceNotFoundException for unknown doctor")
    void getAvailableSlots_unknownDoctor() {
        when(doctorRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> availabilityService.getAvailableSlots(99L, nextMonday, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
