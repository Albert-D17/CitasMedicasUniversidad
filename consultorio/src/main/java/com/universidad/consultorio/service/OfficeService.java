package com.universidad.consultorio.service;

import com.universidad.consultorio.dto.request.CreateOfficeRequest;
import com.universidad.consultorio.dto.request.UpdateOfficeRequest;
import com.universidad.consultorio.dto.response.OfficeResponse;

import java.util.List;

public interface OfficeService {
    OfficeResponse create(CreateOfficeRequest request);
    List<OfficeResponse> findAll();
    OfficeResponse update(Long id, UpdateOfficeRequest request);
}
