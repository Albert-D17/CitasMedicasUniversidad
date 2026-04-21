package com.universidad.consultorio.repository;

import com.universidad.consultorio.entity.Doctor;
import com.universidad.consultorio.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DoctorRepositoryIntegrationTest {

    @Autowired private DoctorRepository doctorRepository;
    @Autowired private SpecialtyRepository specialtyRepository;

    private Specialty cardiology;
    private Specialty psychology;

    @BeforeEach
    void setUp() {
        doctorRepository.deleteAll();
        specialtyRepository.deleteAll();

        cardiology = specialtyRepository.save(Specialty.builder().name("Cardiología").build());
        psychology = specialtyRepository.save(Specialty.builder().name("Psicología").build());

        doctorRepository.save(Doctor.builder()
                .firstName("Carlos").lastName("López").licenseNumber("LIC-001")
                .specialty(cardiology).active(true).build());

        doctorRepository.save(Doctor.builder()
                .firstName("María").lastName("Ruiz").licenseNumber("LIC-002")
                .specialty(cardiology).active(false).build());

        doctorRepository.save(Doctor.builder()
                .firstName("Jorge").lastName("Mora").licenseNumber("LIC-003")
                .specialty(psychology).active(true).build());
    }

    @Test
    @DisplayName("findByActiveTrue - returns only active doctors")
    void findByActiveTrue() {
        List<Doctor> active = doctorRepository.findByActiveTrue();
        assertThat(active).hasSize(2);
    }

    @Test
    @DisplayName("findBySpecialtyIdAndActiveTrue - returns active doctors for a specialty")
    void findBySpecialtyIdAndActiveTrue() {
        List<Doctor> result = doctorRepository.findBySpecialtyIdAndActiveTrue(cardiology.getId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLicenseNumber()).isEqualTo("LIC-001");
    }

    @Test
    @DisplayName("findBySpecialtyIdAndActiveTrue - returns empty when specialty has no active doctors")
    void findBySpecialtyIdAndActiveTrue_noActive() {
        // create a new specialty with no doctors
        Specialty empty = specialtyRepository.save(Specialty.builder().name("Nutrición").build());
        List<Doctor> result = doctorRepository.findBySpecialtyIdAndActiveTrue(empty.getId());
        assertThat(result).isEmpty();
    }
}
