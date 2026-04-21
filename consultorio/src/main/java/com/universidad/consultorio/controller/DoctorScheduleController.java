package com.universidad.consultorio.controller;

import com.universidad.consultorio.dto.request.CreateDoctorScheduleRequest;
import com.universidad.consultorio.dto.response.DoctorScheduleResponse;
import com.universidad.consultorio.service.DoctorScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors/{doctorId}/schedules")
@RequiredArgsConstructor
public class DoctorScheduleController {

    private final DoctorScheduleService doctorScheduleService;

    @PostMapping
    public ResponseEntity<DoctorScheduleResponse> create(
            @PathVariable Long doctorId,
            @Valid @RequestBody CreateDoctorScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(doctorScheduleService.create(doctorId, request));
    }

    @GetMapping
    public ResponseEntity<List<DoctorScheduleResponse>> findByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorScheduleService.findByDoctor(doctorId));
    }
}
