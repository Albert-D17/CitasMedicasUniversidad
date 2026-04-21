package com.universidad.consultorio.repository;

import com.universidad.consultorio.entity.*;
import com.universidad.consultorio.enums.AppointmentStatus;
import com.universidad.consultorio.enums.OfficeStatus;
import com.universidad.consultorio.enums.PatientStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AppointmentRepositoryIntegrationTest {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private OfficeRepository officeRepository;
    @Autowired private AppointmentTypeRepository appointmentTypeRepository;
    @Autowired private SpecialtyRepository specialtyRepository;

    private Patient patient;
    private Doctor doctor;
    private Office office;
    private AppointmentType appointmentType;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();

        Specialty specialty = specialtyRepository.save(Specialty.builder().name("Medicina").build());
        patient = patientRepository.save(Patient.builder()
                .firstName("Ana").lastName("Torres").documentNumber("11111111")
                .status(PatientStatus.ACTIVE).build());
        doctor = doctorRepository.save(Doctor.builder()
                .firstName("Pedro").lastName("Gil").licenseNumber("LIC-100")
                .specialty(specialty).active(true).build());
        office = officeRepository.save(Office.builder()
                .name("Consultorio 1").status(OfficeStatus.ACTIVE).build());
        appointmentType = appointmentTypeRepository.save(AppointmentType.builder()
                .name("Consulta General").durationMinutes(30).build());
    }

    private Appointment saveAppointment(LocalDateTime start, LocalDateTime end, AppointmentStatus status) {
        return appointmentRepository.save(Appointment.builder()
                .patient(patient).doctor(doctor).office(office)
                .appointmentType(appointmentType)
                .startAt(start).endAt(end).status(status).build());
    }

    @Test
    @DisplayName("existsDoctorOverlap - detects overlap for same doctor")
    void existsDoctorOverlap_overlap() {
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
        saveAppointment(start, start.plusMinutes(30), AppointmentStatus.SCHEDULED);

        boolean overlap = appointmentRepository.existsDoctorOverlap(
                doctor.getId(), start.plusMinutes(15), start.plusMinutes(45), null);
        assertThat(overlap).isTrue();
    }

    @Test
    @DisplayName("existsDoctorOverlap - no overlap when time is after existing appointment")
    void existsDoctorOverlap_noOverlap() {
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
        saveAppointment(start, start.plusMinutes(30), AppointmentStatus.SCHEDULED);

        boolean overlap = appointmentRepository.existsDoctorOverlap(
                doctor.getId(), start.plusMinutes(30), start.plusMinutes(60), null);
        assertThat(overlap).isFalse();
    }

    @Test
    @DisplayName("existsOfficeOverlap - detects overlap for same office")
    void existsOfficeOverlap_overlap() {
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        saveAppointment(start, start.plusMinutes(30), AppointmentStatus.CONFIRMED);

        boolean overlap = appointmentRepository.existsOfficeOverlap(
                office.getId(), start.plusMinutes(10), start.plusMinutes(40), null);
        assertThat(overlap).isTrue();
    }

    @Test
    @DisplayName("existsOfficeOverlap - cancelled appointments do not block the office")
    void existsOfficeOverlap_cancelledDoesNotBlock() {
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        saveAppointment(start, start.plusMinutes(30), AppointmentStatus.CANCELLED);

        boolean overlap = appointmentRepository.existsOfficeOverlap(
                office.getId(), start, start.plusMinutes(30), null);
        assertThat(overlap).isFalse();
    }

    @Test
    @DisplayName("findByStartAtBetween - returns appointments in date range")
    void findByStartAtBetween() {
        LocalDateTime base = LocalDateTime.now().plusDays(1).withHour(8).withMinute(0).withSecond(0).withNano(0);
        saveAppointment(base, base.plusMinutes(30), AppointmentStatus.SCHEDULED);
        saveAppointment(base.plusDays(5), base.plusDays(5).plusMinutes(30), AppointmentStatus.SCHEDULED);

        List<Appointment> results = appointmentRepository.findByStartAtBetween(
                base.minusHours(1), base.plusDays(2));
        assertThat(results).hasSize(1);
    }

    @Test
    @DisplayName("findOfficeOccupancy - groups appointments by office correctly")
    void findOfficeOccupancy() {
        LocalDateTime base = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
        saveAppointment(base, base.plusMinutes(30), AppointmentStatus.CONFIRMED);
        saveAppointment(base.plusMinutes(30), base.plusMinutes(60), AppointmentStatus.COMPLETED);

        List<Object[]> results = appointmentRepository.findOfficeOccupancy(
                base.minusHours(1), base.plusDays(1));
        assertThat(results).hasSize(1);
        assertThat(((Number) results.get(0)[2]).longValue()).isEqualTo(2L);
    }
}
