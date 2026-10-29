package com.uam.tecnouam.prestamos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EquipoRequest(
        @NotBlank(message = "El código patrimonial es obligatorio")
        @Size(max = 50, message = "El código patrimonial no puede superar 50 caracteres")
        String codigoPatrimonial,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        String nombre,

        @NotBlank(message = "El tipo es obligatorio")
        @Size(max = 50, message = "El tipo no puede superar 50 caracteres")
        String tipo) {
}
