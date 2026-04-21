package com.universidad.consultorio.service.impl;

import com.universidad.consultorio.dto.response.DoctorProductivityResponse;
import com.universidad.consultorio.dto.response.NoShowPatientResponse;
import com.universidad.consultorio.dto.response.OfficeOccupancyResponse;
import com.universidad.consultorio.repository.AppointmentRepository;
import com.universidad.consultorio.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OfficeOccupancyResponse> getOfficeOccupancy(LocalDate from, LocalDate to) {
        return appointmentRepository
                .findOfficeOccupancy(from.atStartOfDay(), to.plusDays(1).atStartOfDay())
                .stream()
                .map(row -> OfficeOccupancyResponse.builder()
                        .officeId(((Number) row[0]).longValue())
                        .officeName((String) row[1])
                        .appointmentCount(((Number) row[2]).longValue())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorProductivityResponse> getDoctorProductivity(LocalDate from, LocalDate to) {
        return appointmentRepository
                .findDoctorProductivity(from.atStartOfDay(), to.plusDays(1).atStartOfDay())
                .stream()
                .map(row -> DoctorProductivityResponse.builder()
                        .doctorId(((Number) row[0]).longValue())
                        .doctorName(row[1] + " " + row[2])
                        .specialtyName((String) row[3])
                        .completedAppointments(((Number) row[4]).longValue())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoShowPatientResponse> getNoShowPatients(LocalDate from, LocalDate to) {
        return appointmentRepository
                .findNoShowPatients(from.atStartOfDay(), to.plusDays(1).atStartOfDay())
                .stream()
                .map(row -> NoShowPatientResponse.builder()
                        .patientId(((Number) row[0]).longValue())
                        .patientName(row[1] + " " + row[2])
                        .documentNumber((String) row[3])
                        .noShowCount(((Number) row[4]).longValue())
                        .build())
                .toList();
    }
}
