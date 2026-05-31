
package com.universidad.consultorio.repository;

import com.universidad.consultorio.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SpecialtyRepositoryTest {

    @Autowired private SpecialtyRepository specialtyRepository;

    private Specialty specialty;

    @BeforeEach
    void setUp() {
        specialty = specialtyRepository.save(Specialty.builder()
                .name("Cardiología")
                .description("Especialidad del corazón")
                .build());
    }

    @Test
    @DisplayName("findByNameIgnoreCase - encuentra especialidad por nombre ignorando mayúsculas")
    void shouldFindByNameIgnoreCase() {
        var found = specialtyRepository.findByNameIgnoreCase("cardiología");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Cardiología");
    }

    @Test
    @DisplayName("findByNameIgnoreCase - encuentra especialidad con mayúsculas")
    void shouldFindByNameIgnoreCaseWithUppercase() {
        var found = specialtyRepository.findByNameIgnoreCase("CARDIOLOGÍA");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Cardiología");
    }

    @Test
    @DisplayName("findByNameIgnoreCase - retorna Optional vacío si no existe la especialidad")
    void shouldReturnEmptyWhenNameNotFound() {
        var found = specialtyRepository.findByNameIgnoreCase("Dermatología");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("save - guarda una especialidad correctamente")
    void shouldSaveSpecialty() {
        var newSpecialty = Specialty.builder()
                .name("Neurología")
                .description("Estudio del sistema nervioso")
                .build();

        var saved = specialtyRepository.save(newSpecialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Neurología");
    }

    @Test
    @DisplayName("findById - encuentra especialidad por ID")
    void shouldFindById() {
        var found = specialtyRepository.findById(specialty.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Cardiología");
    }
}