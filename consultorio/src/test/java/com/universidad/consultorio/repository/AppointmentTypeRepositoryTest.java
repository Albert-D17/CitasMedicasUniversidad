
package com.universidad.consultorio.repository;

import com.universidad.consultorio.entity.AppointmentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AppointmentTypeRepositoryTest {

    @Autowired private AppointmentTypeRepository appointmentTypeRepository;

    private AppointmentType appointmentType;

    @BeforeEach
    void setUp() {
        appointmentType = appointmentTypeRepository.save(AppointmentType.builder()
                .name("Consulta General")
                .durationMinutes(30)
                .description("Consulta médica general")
                .build());
    }

    @Test
    @DisplayName("save - guarda un tipo de cita correctamente")
    void shouldSaveAppointmentType() {
        var newType = AppointmentType.builder()
                .name("Cardiología")
                .durationMinutes(45)
                .description("Consulta con especialista")
                .build();

        var saved = appointmentTypeRepository.save(newType);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Cardiología");
        assertThat(saved.getDurationMinutes()).isEqualTo(45);
    }

    @Test
    @DisplayName("findById - encuentra un tipo de cita por ID")
    void shouldFindById() {
        var found = appointmentTypeRepository.findById(appointmentType.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Consulta General");
    }

    @Test
    @DisplayName("findAll - retorna todos los tipos de cita")
    void shouldFindAll() {
        appointmentTypeRepository.save(AppointmentType.builder()
                .name("Urgencia")
                .durationMinutes(20)
                .build());

        var results = appointmentTypeRepository.findAll();

        assertThat(results).hasSize(2);
    }

    @Test
    @DisplayName("deleteById - elimina un tipo de cita")
    void shouldDeleteById() {
        appointmentTypeRepository.deleteById(appointmentType.getId());

        var found = appointmentTypeRepository.findById(appointmentType.getId());

        assertThat(found).isEmpty();
    }
}