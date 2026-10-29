package com.uam.tecnouam.prestamos.dto;

import com.uam.tecnouam.prestamos.model.EstadoEquipo;

public record EquipoResponse(
        Long id,
        String codigoPatrimonial,
        String nombre,
        String tipo,
        EstadoEquipo estado) {
}
