package com.universidad.consultorio.controller;

import com.universidad.consultorio.dto.response.DoctorProductivityResponse;
import com.universidad.consultorio.dto.response.NoShowPatientResponse;
import com.universidad.consultorio.dto.response.OfficeOccupancyResponse;
import com.universidad.consultorio.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Validated
public class ReportController {

    private final ReportService service;

    @GetMapping("/office-occupancy")
    public ResponseEntity<Page<OfficeOccupancyResponse>> officeOccupancy(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var result = service.getOfficeOccupancy(from, to, PageRequest.of(page, size, Sort.by("id").ascending()));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/doctor-productivity")
    public ResponseEntity<Page<DoctorProductivityResponse>> doctorProductivity(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var result = service.getDoctorProductivity(from, to, PageRequest.of(page, size, Sort.by("id").ascending()));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/no-show-patients")
    public ResponseEntity<Page<NoShowPatientResponse>> noShowPatients(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var result = service.getNoShowPatients(from, to, PageRequest.of(page, size, Sort.by("id").ascending()));
        return ResponseEntity.ok(result);
    }
}