
package com.universidad.consultorio.repository;

import com.universidad.consultorio.entity.Patient;
import com.universidad.consultorio.enums.PatientStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PatientRepositoryTest {

    @Autowired private PatientRepository patientRepository;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = patientRepository.save(Patient.builder()
                .firstName("Ana")
                .lastName("Torres")
                .documentNumber("11111111")
                .email("ana@test.com")
                .phone("3001234567")
                .status(PatientStatus.ACTIVE)
                .build());
    }

    @Test
    @DisplayName("findByDocumentNumber - encuentra paciente por número de documento")
    void shouldFindByDocumentNumber() {
        var found = patientRepository.findByDocumentNumber("11111111");

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Ana");
        assertThat(found.get().getDocumentNumber()).isEqualTo("11111111");
    }

    @Test
    @DisplayName("findByDocumentNumber - retorna Optional vacío si no existe el documento")
    void shouldReturnEmptyWhenDocumentNumberNotFound() {
        var found = patientRepository.findByDocumentNumber("99999999");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findByStatus - encuentra pacientes por estado")
    void shouldFindByStatus() {
        var inactivePatient = patientRepository.save(Patient.builder()
                .firstName("Inactivo")
                .lastName("Test")
                .documentNumber("22222222")
                .email("inactive@test.com")
                .phone("3000000000")
                .status(PatientStatus.INACTIVE)
                .build());

        var activePatients = patientRepository.findByStatus(PatientStatus.ACTIVE);
        var inactivePatients = patientRepository.findByStatus(PatientStatus.INACTIVE);

        assertThat(activePatients).hasSize(1);
        assertThat(activePatients.get(0).getFirstName()).isEqualTo("Ana");
        assertThat(inactivePatients).hasSize(1);
        assertThat(inactivePatients.get(0).getFirstName()).isEqualTo("Inactivo");
    }

    @Test
    @DisplayName("save - guarda un paciente correctamente")
    void shouldSavePatient() {
        var newPatient = Patient.builder()
                .firstName("Carlos")
                .lastName("Ruiz")
                .documentNumber("33333333")
                .email("carlos@test.com")
                .phone("3010000000")
                .status(PatientStatus.ACTIVE)
                .build();

        var saved = patientRepository.save(newPatient);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Carlos");
        assertThat(saved.getDocumentNumber()).isEqualTo("33333333");
    }

    @Test
    @DisplayName("findById - encuentra paciente por ID")
    void shouldFindById() {
        var found = patientRepository.findById(patient.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Ana");
    }
}