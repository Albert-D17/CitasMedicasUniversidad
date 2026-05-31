@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean ReportService service;

    @Test
    void officeOccupancy_shouldReturn200() throws Exception {
        when(service.getOfficeOccupancy(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(
                        OfficeOccupancyResponse.builder()
                                .officeId(1L).officeName("Office A").appointmentCount(5L)
                                .build())));

        mockMvc.perform(get("/api/reports/office-occupancy")
                        .param("from", "2025-01-01")
                        .param("to", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].officeId").value(1L));
    }

    @Test
    void doctorProductivity_shouldReturn200() throws Exception {
        when(service.getDoctorProductivity(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(
                        DoctorProductivityResponse.builder()
                                .doctorId(1L).doctorName("Dr. Smith")
                                .specialtyName("Cardiology").completedAppointments(10L)
                                .build())));

        mockMvc.perform(get("/api/reports/doctor-productivity")
                        .param("from", "2025-01-01")
                        .param("to", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].doctorId").value(1L));
    }

    @Test
    void noShowPatients_shouldReturn200() throws Exception {
        when(service.getNoShowPatients(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(
                        NoShowPatientResponse.builder()
                                .patientId(1L).patientName("Juan Perez")
                                .documentNumber("123456").noShowCount(3L)
                                .build())));

        mockMvc.perform(get("/api/reports/no-show-patients")
                        .param("from", "2025-01-01")
                        .param("to", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].patientId").value(1L));
    }
}