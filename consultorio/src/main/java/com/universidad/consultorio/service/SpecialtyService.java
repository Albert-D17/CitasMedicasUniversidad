package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.Request.CreateSpecialtyRequest;
import com.universidad.consultorio.dto.Response.SpecialtyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SpecialtyService {
    SpecialtyResponse create(CreateSpecialtyRequest request);
    Page<SpecialtyResponse> findAll(Pageable page);
    SpecialtyResponse findById(Long id);
}
