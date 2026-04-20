package com.rulloa.s3d.reservamedica.service;

import com.rulloa.s3d.reservamedica.model.Profesional;
import com.rulloa.s3d.reservamedica.repository.ProfesionalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProfesionalServiceImpl implements ProfesionalService {
    private final ProfesionalRepository repo;

    public ProfesionalServiceImpl(ProfesionalRepository repo) { this.repo = repo; }

    @Override
    public List<Profesional> listarProfesionales() { return repo.findAll(); }

    @Override
    public Optional<Profesional> obtenerProfesional(Integer id) { return repo.findById(id); }

    @Override
    public Profesional guardarProfesional(Profesional profesional) { return repo.save(profesional); }

    @Override
    public void eliminarProfesional(Integer id) {
        if (!repo.existsById(id)) throw new jakarta.persistence.EntityNotFoundException("Profesional no encontrado");
        repo.deleteById(id);
    }
}
