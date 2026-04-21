package com.universidad.consultorio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.consultorio.dto.request.CreateDoctorRequest;
import com.universidad.consultorio.dto.response.DoctorResponse;
import com.universidad.consultorio.exception.GlobalExceptionHandler;
import com.universidad.consultorio.exception.ResourceNotFoundException;
import com.universidad.consultorio.service.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DoctorControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock private DoctorService doctorService;
    @InjectMocks private DoctorController doctorController;

    private DoctorResponse sampleDoctor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        sampleDoctor = DoctorResponse.builder()
                .id(1L).firstName("Carlos").lastName("López")
                .licenseNumber("LIC-001").active(true)
                .specialtyId(1L).specialtyName("Cardiología").build();
    }

    @Test
    @DisplayName("POST /api/doctors - 201 on valid request")
    void create_returns201() throws Exception {
        when(doctorService.create(any())).thenReturn(sampleDoctor);

        CreateDoctorRequest req = new CreateDoctorRequest();
        req.setFirstName("Carlos");
        req.setLastName("López");
        req.setLicenseNumber("LIC-001");
        req.setSpecialtyId(1L);

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.licenseNumber").value("LIC-001"));
    }

    @Test
    @DisplayName("POST /api/doctors - 400 when specialtyId is null")
    void create_returns400_noSpecialty() throws Exception {
        CreateDoctorRequest req = new CreateDoctorRequest();
        req.setFirstName("Carlos");
        req.setLastName("López");
        req.setLicenseNumber("LIC-001");
        // specialtyId is null

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/doctors/{id} - 404 when not found")
    void findById_returns404() throws Exception {
        when(doctorService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Doctor not found with id: 99"));

        mockMvc.perform(get("/api/doctors/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/doctors - 200 with full list")
    void findAll_returns200() throws Exception {
        when(doctorService.findAll()).thenReturn(List.of(sampleDoctor));

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
