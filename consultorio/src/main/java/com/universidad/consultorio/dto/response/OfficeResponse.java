package com.universidad.consultorio.dto.response;

import com.universidad.consultorio.enums.OfficeStatus;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class OfficeResponse {
    private Long id;
    private String name;
    private String location;
    private String floor;
    private OfficeStatus status;
}
