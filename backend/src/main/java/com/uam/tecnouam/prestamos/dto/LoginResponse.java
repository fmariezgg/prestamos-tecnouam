package com.uam.tecnouam.prestamos.dto;

import com.uam.tecnouam.prestamos.model.Rol;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresIn,
        String username,
        Rol rol) {
}
