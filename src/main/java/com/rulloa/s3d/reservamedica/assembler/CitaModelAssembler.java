package com.rulloa.s3d.reservamedica.assembler;

import com.rulloa.s3d.reservamedica.controller.CitaController;
import com.rulloa.s3d.reservamedica.model.Cita;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CitaModelAssembler implements RepresentationModelAssembler<Cita, EntityModel<Cita>> {

    @Override
    public EntityModel<Cita> toModel(Cita cita) {
        EntityModel<Cita> model = EntityModel.of(cita,
                linkTo(methodOn(CitaController.class).listar(null, null)).withRel("citas"));

        if (cita.getId() != null && cita.getEstado() == Cita.EstadoCita.PROGRAMADA) {
            model.add(linkTo(methodOn(CitaController.class).cancelar(cita.getId())).withRel("cancelar"));
        }

        return model;
    }
}
