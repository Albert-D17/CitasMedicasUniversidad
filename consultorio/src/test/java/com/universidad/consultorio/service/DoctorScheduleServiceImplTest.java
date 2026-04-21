package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.request.CreateDoctorScheduleRequest;
import com.universidad.consultorio.entity.Doctor;
import com.universidad.consultorio.entity.DoctorSchedule;
import com.universidad.consultorio.entity.Specialty;
import com.universidad.consultorio.exception.BusinessException;
import com.universidad.consultorio.exception.ResourceNotFoundException;
import com.universidad.consultorio.mapper.DoctorScheduleMapper;
import com.universidad.consultorio.repository.DoctorRepository;
import com.universidad.consultorio.repository.DoctorScheduleRepository;
import com.universidad.consultorio.service.impl.DoctorScheduleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorScheduleServiceImplTest {

    @Mock private DoctorScheduleRepository doctorScheduleRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private DoctorScheduleMapper doctorScheduleMapper;

    @InjectMocks
    private DoctorScheduleServiceImpl doctorScheduleService;

    private Doctor activeDoctor;
    private Doctor inactiveDoctor;

    @BeforeEach
    void setUp() {
        Specialty s = Specialty.builder().id(1L).name("Medicina").build();
        activeDoctor   = Doctor.builder().id(1L).firstName("Ana").lastName("Ríos")
                .licenseNumber("LIC-1").specialty(s).active(true).build();
        inactiveDoctor = Doctor.builder().id(2L).firstName("Luis").lastName("Ramos")
                .licenseNumber("LIC-2").specialty(s).active(false).build();
    }

    private CreateDoctorScheduleRequest buildRequest(LocalTime start, LocalTime end) {
        CreateDoctorScheduleRequest req = new CreateDoctorScheduleRequest();
        req.setDayOfWeek(DayOfWeek.MONDAY);
        req.setStartTime(start);
        req.setEndTime(end);
        return req;
    }

    @Test
    @DisplayName("create - throws ResourceNotFoundException when doctor not found")
    void create_doctorNotFound() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> doctorScheduleService.create(99L, buildRequest(
                LocalTime.of(8, 0), LocalTime.of(12, 0))))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("create - throws BusinessException when doctor is inactive")
    void create_inactiveDoctor() {
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(inactiveDoctor));
        assertThatThrownBy(() -> doctorScheduleService.create(2L, buildRequest(
                LocalTime.of(8, 0), LocalTime.of(12, 0))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("inactive");
    }

    @Test
    @DisplayName("create - throws BusinessException when start time equals end time")
    void create_startEqualsEnd() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(activeDoctor));
        assertThatThrownBy(() -> doctorScheduleService.create(1L, buildRequest(
                LocalTime.of(9, 0), LocalTime.of(9, 0))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("before end");
    }

    @Test
    @DisplayName("create - throws BusinessException when start time is after end time")
    void create_startAfterEnd() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(activeDoctor));
        assertThatThrownBy(() -> doctorScheduleService.create(1L, buildRequest(
                LocalTime.of(14, 0), LocalTime.of(9, 0))))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("findByDoctor - throws ResourceNotFoundException when doctor not found")
    void findByDoctor_notFound() {
        when(doctorRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> doctorScheduleService.findByDoctor(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
