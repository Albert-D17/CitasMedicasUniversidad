package com.universidad.consultorio.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOfficeRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String location;
    private String floor;
}
