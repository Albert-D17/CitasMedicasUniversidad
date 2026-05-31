package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.Response.OfficeResponse;
import com.universidad.consultorio.dto.Request.CreateOfficeRequest;
import com.universidad.consultorio.dto.Request.UpdateOfficeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OfficeService {
    OfficeResponse create(CreateOfficeRequest request);
    Page<OfficeResponse> findAll(Pageable page);
    OfficeResponse update(Long id, UpdateOfficeRequest request);
}
