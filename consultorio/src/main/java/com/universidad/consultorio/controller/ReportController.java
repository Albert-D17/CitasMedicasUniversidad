package com.universidad.consultorio.controller;

import com.universidad.consultorio.dto.response.DoctorProductivityResponse;
import com.universidad.consultorio.dto.response.NoShowPatientResponse;
import com.universidad.consultorio.dto.response.OfficeOccupancyResponse;
import com.universidad.consultorio.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/office-occupancy")
    public ResponseEntity<List<OfficeOccupancyResponse>> officeOccupancy(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(reportService.getOfficeOccupancy(from, to));
    }

    @GetMapping("/doctor-productivity")
    public ResponseEntity<List<DoctorProductivityResponse>> doctorProductivity(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(reportService.getDoctorProductivity(from, to));
    }

    @GetMapping("/no-show-patients")
    public ResponseEntity<List<NoShowPatientResponse>> noShowPatients(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(reportService.getNoShowPatients(from, to));
    }
}
