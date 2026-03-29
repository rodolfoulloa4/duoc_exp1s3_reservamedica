package com.rulloa.s3d.reservamedica.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Optional;

public class RepositorioCitas {
    private static final List<Cita> CITAS = new CopyOnWriteArrayList<>();
    private static final AtomicLong SIGUIENTE_ID = new AtomicLong(1);

    public static List<Cita> listar() {
        return CITAS;
    }

    public static Cita guardar(Cita cita) {
        if (cita.getId() == null) {
            cita.setId(SIGUIENTE_ID.getAndIncrement());
        }
        CITAS.add(cita);
        return cita;
    }

    public static Optional<Cita> buscarPorId(Long id) {
        return CITAS.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public static boolean eliminar(Long id) {
        return CITAS.removeIf(c -> c.getId().equals(id));
    }
}
