package com.rulloa.s3d.reservamedica.controller;

import tools.jackson.databind.ObjectMapper;
import com.rulloa.s3d.reservamedica.assembler.CitaModelAssembler;
import com.rulloa.s3d.reservamedica.model.Cita;
import com.rulloa.s3d.reservamedica.model.Profesional;
import com.rulloa.s3d.reservamedica.service.CitaService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CitaController.class)
@Import(CitaModelAssembler.class)
class CitaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CitaService citaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void programarCuandoHaySolapamientoDevuelve400() throws Exception {
        Cita solicitud = new Cita();
        solicitud.setNombre_paciente("Juan Perez");
        solicitud.setInicio(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));

        Profesional profesional = new Profesional();
        profesional.setId(1);
        solicitud.setProfesional(profesional);

        when(citaService.programarCita(any(Cita.class))).thenThrow(new IllegalStateException("El horario solicitado se solapa con una cita existente."));

        mockMvc.perform(post("/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitud)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El horario solicitado se solapa con una cita existente."));
    }

    @Test
    void cancelarCitaInexistenteDevuelve404() throws Exception {
        when(citaService.cancelarCita(99)).thenThrow(new EntityNotFoundException("Cita no encontrada"));

        mockMvc.perform(put("/citas/99/cancelar"))
                .andExpect(status().isNotFound());
    }
}
