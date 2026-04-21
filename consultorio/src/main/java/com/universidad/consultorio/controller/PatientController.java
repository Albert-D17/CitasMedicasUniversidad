package com.universidad.consultorio.controller;

import com.universidad.consultorio.dto.request.CreatePatientRequest;
import com.universidad.consultorio.dto.request.UpdatePatientRequest;
import com.universidad.consultorio.dto.response.PatientResponse;
import com.universidad.consultorio.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponse> create(@Valid @RequestBody CreatePatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<PatientResponse>> findAll() {
        return ResponseEntity.ok(patientService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody UpdatePatientRequest request) {
        return ResponseEntity.ok(patientService.update(id, request));
    }
}
