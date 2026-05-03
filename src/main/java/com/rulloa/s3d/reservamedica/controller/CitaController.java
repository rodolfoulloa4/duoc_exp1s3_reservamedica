package com.rulloa.s3d.reservamedica.controller;

import com.rulloa.s3d.reservamedica.assembler.CitaModelAssembler;
import com.rulloa.s3d.reservamedica.model.Cita;
import com.rulloa.s3d.reservamedica.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/citas")
public class CitaController {

    private final CitaService citaService;
    private final CitaModelAssembler assembler;

    public CitaController(CitaService citaService, CitaModelAssembler assembler) {
        this.citaService = citaService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Cita>> listar(
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) Integer idProfesional) {
        List<EntityModel<Cita>> citas = citaService.listarCitas(fecha, idProfesional).stream()
                .map(assembler::toModel).toList();
        return CollectionModel.of(citas,
                linkTo(methodOn(CitaController.class).listar(fecha, idProfesional)).withSelfRel());
    }

    @PostMapping
    public ResponseEntity<?> programar(@Valid @RequestBody Cita solicitud) {
        try {
            Cita cita = citaService.programarCita(solicitud);
            return ResponseEntity.ok(assembler.toModel(cita));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Integer id) {
        try {
            Cita cita = citaService.cancelarCita(id);
            return ResponseEntity.ok(assembler.toModel(cita));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<CollectionModel<EntityModel<Cita>>> disponibilidad(
            @RequestParam(required = false) String fecha,
            @RequestParam Integer idProfesional) {
        List<EntityModel<Cita>> slots = citaService.buscarDisponibilidad(fecha, idProfesional).stream()
                .map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(slots,
                linkTo(methodOn(CitaController.class).disponibilidad(fecha, idProfesional)).withSelfRel()));
    }
}
