package com.universidad.consultorio.repository;

import com.universidad.consultorio.entity.Doctor;
import com.universidad.consultorio.entity.DoctorSchedule;
import com.universidad.consultorio.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DoctorScheduleRepositoryIntegrationTest {

    @Autowired private DoctorScheduleRepository doctorScheduleRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private SpecialtyRepository specialtyRepository;

    private Doctor doctor;

    @BeforeEach
    void setUp() {
        doctorScheduleRepository.deleteAll();
        doctorRepository.deleteAll();
        specialtyRepository.deleteAll();

        Specialty s = specialtyRepository.save(Specialty.builder().name("Medicina").build());
        doctor = doctorRepository.save(Doctor.builder()
                .firstName("Ana").lastName("Ríos").licenseNumber("LIC-200")
                .specialty(s).active(true).build());

        doctorScheduleRepository.save(DoctorSchedule.builder()
                .doctor(doctor).dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(8, 0)).endTime(LocalTime.of(12, 0)).build());
        doctorScheduleRepository.save(DoctorSchedule.builder()
                .doctor(doctor).dayOfWeek(DayOfWeek.WEDNESDAY)
                .startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(18, 0)).build());
    }

    @Test
    @DisplayName("findByDoctorId - returns all schedules for doctor")
    void findByDoctorId() {
        List<DoctorSchedule> result = doctorScheduleRepository.findByDoctorId(doctor.getId());
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("findByDoctorIdAndDayOfWeek - returns schedule for specific day")
    void findByDoctorIdAndDayOfWeek_found() {
        List<DoctorSchedule> result = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(doctor.getId(), DayOfWeek.MONDAY);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStartTime()).isEqualTo(LocalTime.of(8, 0));
    }

    @Test
    @DisplayName("findByDoctorIdAndDayOfWeek - returns empty when no schedule for that day")
    void findByDoctorIdAndDayOfWeek_notFound() {
        List<DoctorSchedule> result = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(doctor.getId(), DayOfWeek.FRIDAY);
        assertThat(result).isEmpty();
    }
}
