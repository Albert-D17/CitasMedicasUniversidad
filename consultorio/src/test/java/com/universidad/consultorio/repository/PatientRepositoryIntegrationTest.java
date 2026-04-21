package com.universidad.consultorio.repository;

import com.universidad.consultorio.entity.Patient;
import com.universidad.consultorio.enums.PatientStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PatientRepositoryIntegrationTest {

    @Autowired
    private PatientRepository patientRepository;

    private Patient activePatient;
    private Patient inactivePatient;

    @BeforeEach
    void setUp() {
        patientRepository.deleteAll();

        activePatient = patientRepository.save(Patient.builder()
                .firstName("Ana").lastName("García")
                .documentNumber("12345678").email("ana@test.com")
                .status(PatientStatus.ACTIVE).build());

        inactivePatient = patientRepository.save(Patient.builder()
                .firstName("Luis").lastName("Pérez")
                .documentNumber("87654321").email("luis@test.com")
                .status(PatientStatus.INACTIVE).build());
    }

    @Test
    @DisplayName("findByDocumentNumber - returns patient when document exists")
    void findByDocumentNumber_exists() {
        Optional<Patient> result = patientRepository.findByDocumentNumber("12345678");
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Ana");
    }

    @Test
    @DisplayName("findByDocumentNumber - returns empty when document not found")
    void findByDocumentNumber_notFound() {
        Optional<Patient> result = patientRepository.findByDocumentNumber("99999999");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByStatus ACTIVE - returns only active patients")
    void findByStatus_active() {
        List<Patient> actives = patientRepository.findByStatus(PatientStatus.ACTIVE);
        assertThat(actives).hasSize(1);
        assertThat(actives.get(0).getDocumentNumber()).isEqualTo("12345678");
    }

    @Test
    @DisplayName("findByStatus INACTIVE - returns only inactive patients")
    void findByStatus_inactive() {
        List<Patient> inactives = patientRepository.findByStatus(PatientStatus.INACTIVE);
        assertThat(inactives).hasSize(1);
        assertThat(inactives.get(0).getDocumentNumber()).isEqualTo("87654321");
    }
}
