package com.universidad.consultorio.controller;

import com.universidad.consultorio.dto.request.CreateSpecialtyRequest;
import com.universidad.consultorio.dto.response.SpecialtyResponse;
import com.universidad.consultorio.service.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @PostMapping
    public ResponseEntity<SpecialtyResponse> create(@Valid @RequestBody CreateSpecialtyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(specialtyService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyResponse>> findAll() {
        return ResponseEntity.ok(specialtyService.findAll());
    }
}
