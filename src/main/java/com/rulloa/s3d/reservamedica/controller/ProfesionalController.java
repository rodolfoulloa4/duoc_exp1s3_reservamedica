package com.rulloa.s3d.reservamedica.controller;

import com.rulloa.s3d.reservamedica.assembler.ProfesionalModelAssembler;
import com.rulloa.s3d.reservamedica.model.Profesional;
import com.rulloa.s3d.reservamedica.service.ProfesionalService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/profesionales")
public class ProfesionalController {
    private final ProfesionalService service;
    private final ProfesionalModelAssembler assembler;

    public ProfesionalController(ProfesionalService service, ProfesionalModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Profesional>> listar() {
        List<EntityModel<Profesional>> profesionales = service.listarProfesionales().stream()
                .map(assembler::toModel).toList();
        return CollectionModel.of(profesionales,
                linkTo(methodOn(ProfesionalController.class).listar()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Profesional>> obtener(@PathVariable Integer id) {
        return service.obtenerProfesional(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<Profesional>> crear(@Valid @RequestBody Profesional profesional) {
        Profesional saved = service.guardarProfesional(profesional);
        EntityModel<Profesional> model = assembler.toModel(saved);
        return ResponseEntity
                .created(linkTo(methodOn(ProfesionalController.class).obtener(saved.getId())).toUri())
                .body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Profesional>> editar(@PathVariable Integer id, @Valid @RequestBody Profesional profesional) {
        return service.obtenerProfesional(id).map(existing -> {
            profesional.setId(id);
            Profesional updated = service.guardarProfesional(profesional);
            return ResponseEntity.ok(assembler.toModel(updated));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!service.obtenerProfesional(id).isPresent()) return ResponseEntity.notFound().build();
        service.eliminarProfesional(id);
        return ResponseEntity.noContent().build();
    }
}
