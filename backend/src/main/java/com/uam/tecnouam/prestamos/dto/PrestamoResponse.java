package com.uam.tecnouam.prestamos.dto;

import com.uam.tecnouam.prestamos.model.EstadoPrestamo;
import java.time.LocalDate;

public record PrestamoResponse(
        Long id,
        EquipoResponse equipo,
        String nombreSolicitante,
        LocalDate fechaPrestamo,
        LocalDate fechaPrevistaDevolucion,
        LocalDate fechaDevolucionReal,
        EstadoPrestamo estado) {
}
