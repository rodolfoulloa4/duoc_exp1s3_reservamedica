package com.rulloa.s3d.reservamedica.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.rulloa.s3d.reservamedica.model.Cita;
import com.rulloa.s3d.reservamedica.repository.CitaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CitaServiceImpl implements CitaService {

    private static final int DURACION_MINUTOS = 30;

    private final CitaRepository citaRepository;

    public CitaServiceImpl(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @Override
    public List<Cita> listarCitas(String fecha, Integer idProfesional) {
        if (fecha == null && idProfesional == null) {
            return citaRepository.findAll();
        }
        LocalDate dia = (fecha != null) ? LocalDate.parse(fecha) : LocalDate.now();
        LocalDateTime inicio = LocalDateTime.of(dia, LocalTime.MIN);
        LocalDateTime fin = LocalDateTime.of(dia, LocalTime.MAX);
        return citaRepository.findByProfesionalAndFecha(idProfesional, inicio, fin);
    }

    @Override
    public Cita programarCita(Cita solicitud) {
        LocalDateTime inicio = solicitud.getInicio();
        LocalDateTime fin = inicio.plusMinutes(DURACION_MINUTOS);
        solicitud.setFin(fin);

        List<Cita> solapadas = citaRepository.findOverlaps(
                solicitud.getProfesional().getId(), inicio, fin);
        if (!solapadas.isEmpty()) {
            throw new IllegalStateException("El horario solicitado se solapa con una cita existente.");
        }

        solicitud.setEstado(Cita.EstadoCita.PROGRAMADA);
        return citaRepository.save(solicitud);
    }

    @Override
    public Cita cancelarCita(Integer id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Cita no encontrada"));
        if (cita.getEstado() == Cita.EstadoCita.CANCELADA) {
            throw new IllegalStateException("La cita ya está cancelada.");
        }
        cita.setEstado(Cita.EstadoCita.CANCELADA);
        return citaRepository.save(cita);
    }

    @Override
    public List<Cita> buscarDisponibilidad(String fecha, Integer idProfesional) {
        LocalDate dia = (fecha != null) ? LocalDate.parse(fecha) : LocalDate.now();
        LocalDateTime cursor = LocalDateTime.of(dia, LocalTime.of(8, 0));
        LocalDateTime finJornada = LocalDateTime.of(dia, LocalTime.of(18, 0));

        List<Cita> resultado = new ArrayList<>();
        while (!cursor.plusMinutes(DURACION_MINUTOS).isAfter(finJornada)) {
            LocalDateTime slotFin = cursor.plusMinutes(DURACION_MINUTOS);
            List<Cita> solapadas = citaRepository.findOverlaps(idProfesional, cursor, slotFin);
            if (solapadas.isEmpty() && cursor.isAfter(LocalDateTime.now())) {
                Cita slot = new Cita();
                slot.setInicio(cursor);
                slot.setFin(slotFin);
                resultado.add(slot);
            }
            cursor = cursor.plusMinutes(DURACION_MINUTOS);
        }
        return resultado;
    }
}
