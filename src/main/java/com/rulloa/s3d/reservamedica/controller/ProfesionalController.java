package com.rulloa.s3d.reservamedica.controller;

import com.rulloa.s3d.reservamedica.model.Profesional;
import com.rulloa.s3d.reservamedica.service.ProfesionalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/profesionales")
public class ProfesionalController {
    private final ProfesionalService service;
    public ProfesionalController(ProfesionalService service) { this.service = service; }

    @GetMapping
    public List<Profesional> listar() { return service.listarProfesionales(); }

    @GetMapping("/{id}")
    public ResponseEntity<Profesional> obtener(@PathVariable Integer id) {
        return service.obtenerProfesional(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Profesional> crear(@Valid @RequestBody Profesional profesional) {
        Profesional saved = service.guardarProfesional(profesional);
        return ResponseEntity.created(URI.create("/api/profesionales/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Profesional> editar(@PathVariable Integer id, @Valid @RequestBody Profesional profesional) {
        return service.obtenerProfesional(id).map(existing -> {
            profesional.setId(id);
            Profesional updated = service.guardarProfesional(profesional);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!service.obtenerProfesional(id).isPresent()) return ResponseEntity.notFound().build();
        service.eliminarProfesional(id);
        return ResponseEntity.noContent().build();
    }
}
