package com.universidad.consultorio.controller;

import com.universidad.consultorio.dto.request.CreateOfficeRequest;
import com.universidad.consultorio.dto.request.UpdateOfficeRequest;
import com.universidad.consultorio.dto.response.OfficeResponse;
import com.universidad.consultorio.service.OfficeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offices")
@RequiredArgsConstructor
public class OfficeController {

    private final OfficeService officeService;

    @PostMapping
    public ResponseEntity<OfficeResponse> create(@Valid @RequestBody CreateOfficeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(officeService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<OfficeResponse>> findAll() {
        return ResponseEntity.ok(officeService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfficeResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateOfficeRequest request) {
        return ResponseEntity.ok(officeService.update(id, request));
    }
}
