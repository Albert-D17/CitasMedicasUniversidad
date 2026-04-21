package com.universidad.consultorio.controller;

import com.universidad.consultorio.dto.response.AvailabilitySlotResponse;
import com.universidad.consultorio.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping("/doctors/{doctorId}")
    public ResponseEntity<List<AvailabilitySlotResponse>> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long appointmentTypeId) {
        return ResponseEntity.ok(availabilityService.getAvailableSlots(doctorId, date, appointmentTypeId));
    }
}
