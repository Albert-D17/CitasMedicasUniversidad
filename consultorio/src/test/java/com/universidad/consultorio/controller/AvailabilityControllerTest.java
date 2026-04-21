package com.universidad.consultorio.controller;

import com.universidad.consultorio.dto.response.AvailabilitySlotResponse;
import com.universidad.consultorio.exception.GlobalExceptionHandler;
import com.universidad.consultorio.exception.ResourceNotFoundException;
import com.universidad.consultorio.service.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityControllerTest {

    private MockMvc mockMvc;

    @Mock private AvailabilityService availabilityService;
    @InjectMocks private AvailabilityController availabilityController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(availabilityController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/availability/doctors/{id} - 200 with available slots")
    void getAvailableSlots_returns200() throws Exception {
        LocalDateTime slotStart = LocalDate.now().plusDays(7).atTime(9, 0);
        List<AvailabilitySlotResponse> slots = List.of(
                AvailabilitySlotResponse.builder().startAt(slotStart).endAt(slotStart.plusMinutes(30)).build(),
                AvailabilitySlotResponse.builder().startAt(slotStart.plusMinutes(30)).endAt(slotStart.plusMinutes(60)).build()
        );
        when(availabilityService.getAvailableSlots(anyLong(), any(LocalDate.class), anyLong()))
                .thenReturn(slots);

        mockMvc.perform(get("/api/availability/doctors/1")
                        .param("date", LocalDate.now().plusDays(7).toString())
                        .param("appointmentTypeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/availability/doctors/{id} - 200 with empty list when no slots")
    void getAvailableSlots_returnsEmpty() throws Exception {
        when(availabilityService.getAvailableSlots(anyLong(), any(LocalDate.class), anyLong()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/availability/doctors/1")
                        .param("date", LocalDate.now().plusDays(7).toString())
                        .param("appointmentTypeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/availability/doctors/{id} - 404 when doctor not found")
    void getAvailableSlots_returns404() throws Exception {
        when(availabilityService.getAvailableSlots(anyLong(), any(LocalDate.class), anyLong()))
                .thenThrow(new ResourceNotFoundException("Doctor not found with id: 99"));

        mockMvc.perform(get("/api/availability/doctors/99")
                        .param("date", LocalDate.now().plusDays(7).toString())
                        .param("appointmentTypeId", "1"))
                .andExpect(status().isNotFound());
    }
}
