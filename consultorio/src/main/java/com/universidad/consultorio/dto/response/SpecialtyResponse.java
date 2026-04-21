package com.universidad.consultorio.dto.response;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class SpecialtyResponse {
    private Long id;
    private String name;
    private String description;
}
