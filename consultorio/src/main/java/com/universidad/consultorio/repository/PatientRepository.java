package com.universidad.consultorio.repository;

import com.universidad.consultorio.entity.Patient;
import com.universidad.consultorio.enums.PatientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByDocumentNumber(String documentNumber);
    List<Patient> findByStatus(PatientStatus status);
}
