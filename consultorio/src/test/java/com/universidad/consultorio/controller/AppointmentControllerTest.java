package com.universidad.consultorio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.universidad.consultorio.dto.request.CancelAppointmentRequest;
import com.universidad.consultorio.dto.request.CreateAppointmentRequest;
import com.universidad.consultorio.dto.response.AppointmentResponse;
import com.universidad.consultorio.enums.AppointmentStatus;
import com.universidad.consultorio.exception.BusinessException;
import com.universidad.consultorio.exception.ConflictException;
import com.universidad.consultorio.exception.GlobalExceptionHandler;
import com.universidad.consultorio.exception.ResourceNotFoundException;
import com.universidad.consultorio.service.AppointmentService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock private AppointmentService appointmentService;
    @InjectMocks private AppointmentController appointmentController;

    private AppointmentResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        sampleResponse = AppointmentResponse.builder()
                .id(1L).status(AppointmentStatus.SCHEDULED)
                .startAt(LocalDateTime.now().plusDays(1))
                .endAt(LocalDateTime.now().plusDays(1).plusMinutes(30))
                .build();
    }

    private CreateAppointmentRequest validRequest() {
        CreateAppointmentRequest req = new CreateAppointmentRequest();
        req.setPatientId(1L);
        req.setDoctorId(1L);
        req.setOfficeId(1L);
        req.setAppointmentTypeId(1L);
        req.setStartAt(LocalDateTime.now().plusDays(1));
        return req;
    }

    @Test
    @DisplayName("POST /api/appointments - 201 on valid request")
    void create_returns201() throws Exception {
        when(appointmentService.create(any())).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    @DisplayName("POST /api/appointments - 400 on missing required fields")
    void create_returns400_missingFields() throws Exception {
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/appointments - 409 on time conflict")
    void create_returns409_conflict() throws Exception {
        when(appointmentService.create(any()))
                .thenThrow(new ConflictException("Doctor already has an appointment in that time range"));

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /api/appointments/{id} - 200 when found")
    void findById_returns200() throws Exception {
        when(appointmentService.findById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/appointments/{id} - 404 when not found")
    void findById_returns404() throws Exception {
        when(appointmentService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Appointment not found with id: 99"));

        mockMvc.perform(get("/api/appointments/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/appointments - 200 with list")
    void findAll_returns200() throws Exception {
        when(appointmentService.findAll()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("PUT /api/appointments/{id}/confirm - 200 on success")
    void confirm_returns200() throws Exception {
        AppointmentResponse confirmed = AppointmentResponse.builder()
                .id(1L).status(AppointmentStatus.CONFIRMED).build();
        when(appointmentService.confirm(1L)).thenReturn(confirmed);

        mockMvc.perform(put("/api/appointments/1/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("PUT /api/appointments/{id}/cancel - 200 with valid reason")
    void cancel_returns200() throws Exception {
        AppointmentResponse cancelled = AppointmentResponse.builder()
                .id(1L).status(AppointmentStatus.CANCELLED).build();
        when(appointmentService.cancel(eq(1L), any())).thenReturn(cancelled);

        CancelAppointmentRequest req = new CancelAppointmentRequest();
        req.setReason("Patient request");

        mockMvc.perform(put("/api/appointments/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("PUT /api/appointments/{id}/cancel - 400 without reason")
    void cancel_returns400_noReason() throws Exception {
        mockMvc.perform(put("/api/appointments/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/appointments/{id}/complete - 400 when not yet started (BusinessException)")
    void complete_returns400_notStarted() throws Exception {
        when(appointmentService.complete(eq(1L), any()))
                .thenThrow(new BusinessException("Cannot complete an appointment before its scheduled start time"));

        mockMvc.perform(put("/api/appointments/1/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
