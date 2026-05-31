@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean PatientService service;

    private ObjectMapper mapper;
    private PatientResponse response;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        response = PatientResponse.builder()
                .id(1L).firstName("Juan").lastName("Perez")
                .documentNumber("123456").email("juan@mail.com")
                .build();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        CreatePatientRequest req = new CreatePatientRequest();
        req.setFirstName("Juan");
        req.setLastName("Perez");
        req.setDocumentNumber("123456");

        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Juan"));
    }

    @Test
    void create_shouldReturn400_whenFieldsMissing() throws Exception {
        CreatePatientRequest req = new CreatePatientRequest();

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn400_whenInvalidEmail() throws Exception {
        CreatePatientRequest req = new CreatePatientRequest();
        req.setFirstName("Juan");
        req.setLastName("Perez");
        req.setDocumentNumber("123456");
        req.setEmail("not-an-email"); // email inválido

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById_shouldReturn200() throws Exception {
        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void findAll_shouldReturn200() throws Exception {
        when(service.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void update_shouldReturn200() throws Exception {
        UpdatePatientRequest req = new UpdatePatientRequest();
        req.setFirstName("Updated");

        response.setFirstName("Updated");
        when(service.update(eq(1L), any())).thenReturn(response);

        mockMvc.perform(patch("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"));
    }
}