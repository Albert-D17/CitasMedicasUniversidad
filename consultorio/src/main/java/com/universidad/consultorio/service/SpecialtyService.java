package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.request.CreateSpecialtyRequest;
import com.universidad.consultorio.dto.response.SpecialtyResponse;

import java.util.List;

public interface SpecialtyService {
    SpecialtyResponse create(CreateSpecialtyRequest request);
    List<SpecialtyResponse> findAll();
    SpecialtyResponse findById(Long id);
}
