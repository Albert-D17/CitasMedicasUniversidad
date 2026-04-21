package com.universidad.consultorio.controller;

import com.universidad.consultorio.dto.request.CreateDoctorRequest;
import com.universidad.consultorio.dto.request.UpdateDoctorRequest;
import com.universidad.consultorio.dto.response.DoctorResponse;
import com.universidad.consultorio.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorResponse> create(@Valid @RequestBody CreateDoctorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> findAll(
            @RequestParam(required = false) Long specialtyId) {
        if (specialtyId != null) {
            return ResponseEntity.ok(doctorService.findBySpecialty(specialtyId));
        }
        return ResponseEntity.ok(doctorService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateDoctorRequest request) {
        return ResponseEntity.ok(doctorService.update(id, request));
    }
}
