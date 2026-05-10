package com.rulloa.s3d.reservamedica.service;

import com.rulloa.s3d.reservamedica.model.Cita;
import com.rulloa.s3d.reservamedica.repository.CitaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitaServiceImplTest {

    @Mock
    private CitaRepository citaRepository;

    @InjectMocks
    private CitaServiceImpl citaService;

    @Test
    void listarCitasSinFiltrosUsaFindAll() {
        List<Cita> esperadas = List.of(new Cita());
        when(citaRepository.findAll()).thenReturn(esperadas);

        List<Cita> resultado = citaService.listarCitas(null, null);

        assertSame(esperadas, resultado);
        verify(citaRepository).findAll();
        verify(citaRepository, never()).findByProfesionalAndFecha(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void cancelarCitaInexistenteLanzaEntityNotFoundException() {
        when(citaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> citaService.cancelarCita(99));
        verify(citaRepository, never()).save(org.mockito.ArgumentMatchers.any(Cita.class));
    }
}
