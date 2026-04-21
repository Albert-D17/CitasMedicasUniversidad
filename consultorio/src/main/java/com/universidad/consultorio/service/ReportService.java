package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.response.DoctorProductivityResponse;
import com.universidad.consultorio.dto.response.NoShowPatientResponse;
import com.universidad.consultorio.dto.response.OfficeOccupancyResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<OfficeOccupancyResponse> getOfficeOccupancy(LocalDate from, LocalDate to);
    List<DoctorProductivityResponse> getDoctorProductivity(LocalDate from, LocalDate to);
    List<NoShowPatientResponse> getNoShowPatients(LocalDate from, LocalDate to);
}
