package com.universidad.consultorio.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSpecialtyRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
}
