
package com.universidad.consultorio.repository;

import com.universidad.consultorio.entity.Doctor;
import com.universidad.consultorio.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DoctorRepositoryTest {

    @Autowired private DoctorRepository doctorRepository;
    @Autowired private SpecialtyRepository specialtyRepository;

    private Specialty specialty;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        specialty = specialtyRepository.save(Specialty.builder()
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
    }

    @Test
    @DisplayName("findByActiveTrue - encuentra solo doctores activos")
    void shouldFindActiveDoctors() {
        var inactiveDoctor = doctorRepository.save(Doctor.builder()
                .firstName("Inactivo")
                .lastName("Test")
                .licenseNumber("LIC-999")
                .email("inactive@test.com")
                .specialty(specialty)
                .active(false)
                .build());

        var result = doctorRepository.findByActiveTrue();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(doctor.getId());
        assertThat(result.get(0).isActive()).isTrue();
    }

    @Test
    @DisplayName("findBySpecialtyIdAndActiveTrue - encuentra doctores activos por especialidad con paginación")
    void shouldFindActiveDoctorsBySpecialtyWithPagination() {
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        var result = doctorRepository.findBySpecialtyIdAndActiveTrue(specialty.getId(), pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSpecialty().getId()).isEqualTo(specialty.getId());
        assertThat(result.get(0).isActive()).isTrue();
    }

    @Test
    @DisplayName("findBySpecialtyIdAndActiveTrue - retorna lista vacía si no hay doctores activos")
    void shouldReturnEmptyWhenNoActiveDoctors() {
        var anotherSpecialty = specialtyRepository.save(Specialty.builder()
                .name("Dermatología")
                .build());
        var pageable = PageRequest.of(0, 10);

        var result = doctorRepository.findBySpecialtyIdAndActiveTrue(anotherSpecialty.getId(), pageable);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("save - guarda un doctor correctamente")
    void shouldSaveDoctor() {
        var newDoctor = Doctor.builder()
                .firstName("Maria")
                .lastName("Lopez")
                .licenseNumber("LIC-200")
                .email("maria@test.com")
                .specialty(specialty)
                .active(true)
                .build();

        var saved = doctorRepository.save(newDoctor);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Maria");
        assertThat(saved.getLicenseNumber()).isEqualTo("LIC-200");
    }
}