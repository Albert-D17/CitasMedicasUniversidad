package com.universidad.consultorio.dto.response;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class OfficeOccupancyResponse {
    private Long officeId;
    private String officeName;
    private Long appointmentCount;
}
