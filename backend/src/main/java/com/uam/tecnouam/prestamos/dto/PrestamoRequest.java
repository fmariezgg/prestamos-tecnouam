package com.uam.tecnouam.prestamos.dto;

import com.uam.tecnouam.prestamos.dto.validation.FechasPrestamoValidas;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@FechasPrestamoValidas
public record PrestamoRequest(
        @NotNull(message = "El equipo es obligatorio")
        Long equipoId,

        @NotBlank(message = "El nombre del solicitante es obligatorio")
        @Size(max = 120, message = "El nombre del solicitante no puede superar 120 caracteres")
        String nombreSolicitante,

        @NotNull(message = "La fecha de préstamo es obligatoria")
        LocalDate fechaPrestamo,

        @NotNull(message = "La fecha prevista de devolución es obligatoria")
        LocalDate fechaPrevistaDevolucion) {
}
