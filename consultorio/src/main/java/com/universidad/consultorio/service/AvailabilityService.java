package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.response.AvailabilitySlotResponse;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {
    List<AvailabilitySlotResponse> getAvailableSlots(Long doctorId, LocalDate date, Long appointmentTypeId);
}
