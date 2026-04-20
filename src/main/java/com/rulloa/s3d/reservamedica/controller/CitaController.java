package com.rulloa.s3d.reservamedica.controller;

import com.rulloa.s3d.reservamedica.model.Cita;
import com.rulloa.s3d.reservamedica.service.CitaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public List<Cita> listar(
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) Integer idProfesional) {
        return citaService.listarCitas(fecha, idProfesional);
    }

    @PostMapping
    public ResponseEntity<?> programar(@RequestBody Cita solicitud) {
        try {
            Cita cita = citaService.programarCita(solicitud);
            return ResponseEntity.ok(cita);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Integer id) {
        try {
            Cita cita = citaService.cancelarCita(id);
            return ResponseEntity.ok(cita);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<?> disponibilidad(
            @RequestParam(required = false) String fecha,
            @RequestParam Integer idProfesional) {
        List<Cita> slots = citaService.buscarDisponibilidad(fecha, idProfesional);
        return ResponseEntity.ok(slots);
    }
}
