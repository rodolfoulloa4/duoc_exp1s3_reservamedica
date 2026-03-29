package com.rulloa.s3d.reservamedica.controller;

import com.rulloa.s3d.reservamedica.model.Cita;
import com.rulloa.s3d.reservamedica.model.CitaSolicitud;
import com.rulloa.s3d.reservamedica.model.EstadoCita;
import com.rulloa.s3d.reservamedica.model.RepositorioCitas;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/citas")
public class CitaController {
    private static final int DURACION_FIJA_MINUTOS = 30;

    public CitaController() {
        // Agregar 5 citas tomadas para el día actual (30 minutos cada una)
        if (RepositorioCitas.listar().isEmpty()) {
            LocalDateTime base = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0).withNano(0);
            RepositorioCitas.guardar(new Cita(null, "Ignacio Muñoz", base, EstadoCita.PROGRAMADA));
            RepositorioCitas.guardar(new Cita(null, "Catalina Pérez", base.plusHours(1), EstadoCita.PROGRAMADA));
            RepositorioCitas.guardar(new Cita(null, "Sofía González", base.plusHours(2), EstadoCita.PROGRAMADA));
            RepositorioCitas.guardar(new Cita(null, "Benjamín Soto", base.plusHours(3), EstadoCita.PROGRAMADA));
            RepositorioCitas.guardar(new Cita(null, "Valentina Rojas", base.plusHours(4), EstadoCita.PROGRAMADA));
        }
    }

    @PostMapping("/programar")
    public ResponseEntity<?> programar(@RequestBody CitaSolicitud solicitud) {
        if (solicitud.getPaciente() == null || solicitud.getPaciente().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El campo 'paciente' es obligatorio.");
        }
        // Duración fija: 30 minutos
        LocalDateTime inicio;
        try {
            inicio = LocalDateTime.parse(solicitud.getFechaHora());
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Formato de fechaHora inválido. Use ISO-8601, ej. 2026-03-29T10:30");
        }

        if (inicio.isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("La fecha/hora debe ser en el futuro.");
        }

        // validar solapamiento con citas programadas (duración fija)
        List<Cita> ocupadas = RepositorioCitas.listar().stream()
                .filter(c -> c.getEstado() == EstadoCita.PROGRAMADA)
                .collect(Collectors.toList());

        for (Cita existente : ocupadas) {
            if (solapa(inicio, existente.getInicio())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("La hora solicitada se solapa con otra cita.");
            }
        }

        Cita cita = new Cita(null, solicitud.getPaciente(), inicio, EstadoCita.PROGRAMADA);
        RepositorioCitas.guardar(cita);
        return ResponseEntity.ok(cita);
    }

    @GetMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        Optional<Cita> opt = RepositorioCitas.buscarPorId(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada.");
        }
        Cita cita = opt.get();
        if (cita.getEstado() == EstadoCita.CANCELADA) {
            return ResponseEntity.badRequest().body("La cita ya está cancelada.");
        }
        cita.setEstado(EstadoCita.CANCELADA);
        return ResponseEntity.ok("Cita cancelada correctamente.");
    }

    @GetMapping("/listar")
    public List<Cita> listar() {
        return RepositorioCitas.listar();
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<?> disponibilidad(@RequestParam(required = false) String fecha) {
        String diaStr = (fecha == null) ? LocalDate.now().toString() : fecha;
        LocalDate dia;
        try {
            dia = LocalDate.parse(diaStr);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Formato de fecha inválido. Use yyyy-MM-dd");
        }

        LocalTime inicioJornada = LocalTime.of(8, 0);
        LocalTime finJornada = LocalTime.of(18, 0);

        LocalDateTime cursor = LocalDateTime.of(dia, inicioJornada);
        LocalDateTime fin = LocalDateTime.of(dia, finJornada);

        List<String> disponibles = new ArrayList<>();

        boolean esHoy = dia.equals(LocalDate.now());
        while (!cursor.plusMinutes(DURACION_FIJA_MINUTOS).isAfter(fin)) {
            LocalDateTime inicioSlot = cursor;
            boolean ocupado = RepositorioCitas.listar().stream()
                    .filter(c -> c.getEstado() == EstadoCita.PROGRAMADA)
                    .anyMatch(c -> solapa(inicioSlot, c.getInicio()));

            if (!ocupado && (!esHoy || inicioSlot.isAfter(LocalDateTime.now()))) {
                disponibles.add(inicioSlot.toString());
            }
            cursor = cursor.plusMinutes(DURACION_FIJA_MINUTOS);
        }

        return ResponseEntity.ok(disponibles);
    }

    private boolean solapa(LocalDateTime inicio1, LocalDateTime inicio2) {
        LocalDateTime fin1 = inicio1.plusMinutes(DURACION_FIJA_MINUTOS);
        LocalDateTime fin2 = inicio2.plusMinutes(DURACION_FIJA_MINUTOS);
        return inicio1.isBefore(fin2) && inicio2.isBefore(fin1);
    }
}
