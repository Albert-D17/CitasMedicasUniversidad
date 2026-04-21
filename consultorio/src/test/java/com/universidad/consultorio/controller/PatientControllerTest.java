package com.universidad.consultorio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.consultorio.dto.request.CreatePatientRequest;
import com.universidad.consultorio.dto.response.PatientResponse;
import com.universidad.consultorio.enums.PatientStatus;
import com.universidad.consultorio.exception.ConflictException;
import com.universidad.consultorio.exception.GlobalExceptionHandler;
import com.universidad.consultorio.exception.ResourceNotFoundException;
import com.universidad.consultorio.service.PatientService;
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
class PatientControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock private PatientService patientService;
    @InjectMocks private PatientController patientController;

    private PatientResponse samplePatient;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        samplePatient = PatientResponse.builder()
                .id(1L).firstName("Ana").lastName("López")
                .documentNumber("12345678").status(PatientStatus.ACTIVE).build();
    }

    private CreatePatientRequest validRequest() {
        CreatePatientRequest req = new CreatePatientRequest();
        req.setFirstName("Ana");
        req.setLastName("López");
        req.setDocumentNumber("12345678");
        req.setEmail("ana@test.com");
        return req;
    }

    @Test
    @DisplayName("POST /api/patients - 201 on valid request")
    void create_returns201() throws Exception {
        when(patientService.create(any())).thenReturn(samplePatient);

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Ana"));
    }

    @Test
    @DisplayName("POST /api/patients - 400 when firstName is blank")
    void create_returns400_blankName() throws Exception {
        CreatePatientRequest req = validRequest();
        req.setFirstName("");

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/patients - 409 when document already exists")
    void create_returns409_duplicate() throws Exception {
        when(patientService.create(any()))
                .thenThrow(new ConflictException("Patient with document number 12345678 already exists"));

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /api/patients/{id} - 200 when found")
    void findById_returns200() throws Exception {
        when(patientService.findById(1L)).thenReturn(samplePatient);

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/patients/{id} - 404 when not found")
    void findById_returns404() throws Exception {
        when(patientService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Patient not found with id: 99"));

        mockMvc.perform(get("/api/patients/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/patients - 200 with list")
    void findAll_returns200() throws Exception {
        when(patientService.findAll()).thenReturn(List.of(samplePatient));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
