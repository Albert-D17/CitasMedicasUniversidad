package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.Response.AvailabilitySlotResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;

public interface AvailabilityService {
    Page<AvailabilitySlotResponse> getAvailableSlots(Long doctorId, LocalDate date, Long appointmentTypeId, Pageable page);
}
