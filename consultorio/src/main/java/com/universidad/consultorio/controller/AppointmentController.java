package com.universidad.consultorio.controller;

import com.universidad.consultorio.dto.request.CancelAppointmentRequest;
import com.universidad.consultorio.dto.request.CreateAppointmentRequest;
import com.universidad.consultorio.dto.response.AppointmentResponse;
import com.universidad.consultorio.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody CreateAppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> findAll() {
        return ResponseEntity.ok(appointmentService.findAll());
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponse> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.confirm(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancel(
            @PathVariable Long id,
            @Valid @RequestBody CancelAppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.cancel(id, request));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponse> complete(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String observations = body != null ? body.get("observations") : null;
        return ResponseEntity.ok(appointmentService.complete(id, observations));
    }

    @PutMapping("/{id}/no-show")
    public ResponseEntity<AppointmentResponse> noShow(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.markNoShow(id));
    }
}
