package com.rulloa.s3d.reservamedica.assembler;

import com.rulloa.s3d.reservamedica.controller.ProfesionalController;
import com.rulloa.s3d.reservamedica.model.Profesional;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ProfesionalModelAssembler implements RepresentationModelAssembler<Profesional, EntityModel<Profesional>> {

    @Override
    public EntityModel<Profesional> toModel(Profesional profesional) {
        return EntityModel.of(profesional,
                linkTo(methodOn(ProfesionalController.class).obtener(profesional.getId())).withSelfRel(),
                linkTo(methodOn(ProfesionalController.class).listar()).withRel("profesionales"));
    }
}
