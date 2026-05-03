package com.rulloa.s3d.reservamedica.controller;

import tools.jackson.databind.ObjectMapper;
import com.rulloa.s3d.reservamedica.assembler.ProfesionalModelAssembler;
import com.rulloa.s3d.reservamedica.model.Profesional;
import com.rulloa.s3d.reservamedica.service.ProfesionalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfesionalController.class)
@Import(ProfesionalModelAssembler.class)
class ProfesionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfesionalService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProfesionalModelAssembler assembler;

    @Test
    void listarDevuelveHateoas() throws Exception {
        Profesional p = new Profesional(1, "Dr. García", "Cardiología");
        when(service.listarProfesionales()).thenReturn(List.of(p));

        mockMvc.perform(get("/profesionales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.profesionalList[0].nombre").value("Dr. García"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void crearConPayloadInvalidoDevuelve400() throws Exception {
        Profesional invalido = new Profesional(null, "", null);

        mockMvc.perform(post("/profesionales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }
}
