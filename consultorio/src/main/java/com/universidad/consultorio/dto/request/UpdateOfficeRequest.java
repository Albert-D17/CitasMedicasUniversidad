package com.universidad.consultorio.dto.request;

import com.universidad.consultorio.enums.OfficeStatus;
import lombok.Data;

@Data
public class UpdateOfficeRequest {
    private String name;
    private String location;
    private String floor;
    private OfficeStatus status;
}
