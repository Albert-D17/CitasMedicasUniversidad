package com.universidad.consultorio.controller;

import com.universidad.consultorio.dto.request.CreateAppointmentTypeRequest;
import com.universidad.consultorio.dto.response.AppointmentTypeResponse;
import com.universidad.consultorio.service.AppointmentTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointment-types")
@RequiredArgsConstructor
public class AppointmentTypeController {

    private final AppointmentTypeService appointmentTypeService;

    @PostMapping
    public ResponseEntity<AppointmentTypeResponse> create(@Valid @RequestBody CreateAppointmentTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentTypeService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentTypeResponse>> findAll() {
        return ResponseEntity.ok(appointmentTypeService.findAll());
    }
}
