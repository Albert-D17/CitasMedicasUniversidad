package com.universidad.consultorio.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class AvailabilitySlotResponse {
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
