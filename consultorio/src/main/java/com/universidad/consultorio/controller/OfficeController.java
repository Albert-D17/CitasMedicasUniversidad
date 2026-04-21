package com.universidad.consultorio.controller;

import com.universidad.consultorio.dto.request.CreateOfficeRequest;
import com.universidad.consultorio.dto.request.UpdateOfficeRequest;
import com.universidad.consultorio.dto.response.OfficeResponse;
import com.universidad.consultorio.service.OfficeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/offices")
@RequiredArgsConstructor
@Validated
public class OfficeController {

    private final OfficeService service;

    @PostMapping
    public ResponseEntity<OfficeResponse> create(@Valid @RequestBody CreateOfficeRequest req,
                                                 UriComponentsBuilder uriBuilder) {
        var created = service.create(req);
        var location = uriBuilder.path("/api/offices/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<OfficeResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var result = service.findAll(PageRequest.of(page, size, Sort.by("id").ascending()));
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OfficeResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody UpdateOfficeRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }
}