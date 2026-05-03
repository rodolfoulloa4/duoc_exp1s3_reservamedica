package com.rulloa.s3d.reservamedica.service;

import com.rulloa.s3d.reservamedica.model.Profesional;
import com.rulloa.s3d.reservamedica.repository.ProfesionalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfesionalServiceImplTest {

    @Mock
    private ProfesionalRepository repo;

    @InjectMocks
    private ProfesionalServiceImpl service;

    @Test
    void guardarProfesionalActualizaCorrectamente() {
        Profesional p = new Profesional(1, "Dr. López", "Neurología");
        when(repo.save(p)).thenReturn(p);

        Profesional result = service.guardarProfesional(p);

        assertEquals("Dr. López", result.getNombre());
        verify(repo).save(p);
    }

    @Test
    void eliminarProfesionalInexistenteLanzaExcepcion() {
        when(repo.existsById(99)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.eliminarProfesional(99));
        verify(repo, never()).deleteById(99);
    }
}
