package com.universidad.consultorio.repository;

import com.universidad.consultorio.entity.Doctor;
import com.universidad.consultorio.entity.DoctorSchedule;
import com.universidad.consultorio.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DoctorScheduleRepositoryTest {

    @Autowired private DoctorScheduleRepository doctorScheduleRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private SpecialtyRepository specialtyRepository;

    private Doctor doctor;
    private DoctorSchedule schedule;

    @BeforeEach
    void setUp() {
        var specialty = specialtyRepository.save(Specialty.builder()
                .name("Cardiología")
                .build());

        doctor = doctorRepository.save(Doctor.builder()
                .firstName("Pedro")
                .lastName("Gil")
                .licenseNumber("LIC-100")
                .email("pedro@test.com")
                .specialty(specialty)
                .active(true)
                .build());

        schedule = doctorScheduleRepository.save(DoctorSchedule.builder()
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build());
    }

    @Test
    @DisplayName("findByDoctorId - encuentra horarios por doctor con paginación")
    void shouldFindByDoctorIdWithPagination() {
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        var result = doctorScheduleRepository.findByDoctorId(doctor.getId(), pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getDoctor().getId()).isEqualTo(doctor.getId());
    }

    @Test
    @DisplayName("findByDoctorIdAndDayOfWeek - encuentra horario por doctor y día de semana")
    void shouldFindByDoctorIdAndDayOfWeek() {
        doctorScheduleRepository.save(DoctorSchedule.builder()
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(18, 0))
                .build());

        var result = doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctor.getId(), DayOfWeek.MONDAY);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
    }

    @Test
    @DisplayName("findByDoctorIdAndDayOfWeek - retorna lista vacía si no hay horario para ese día")
    void shouldReturnEmptyWhenNoScheduleForDay() {
        var result = doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctor.getId(), DayOfWeek.SUNDAY);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("save - guarda un horario correctamente")
    void shouldSaveSchedule() {
        var newSchedule = DoctorSchedule.builder()
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.WEDNESDAY)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(12, 0))
                .build();

        var saved = doctorScheduleRepository.save(newSchedule);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDayOfWeek()).isEqualTo(DayOfWeek.WEDNESDAY);
    }
}