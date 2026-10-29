package com.uam.tecnouam.prestamos.mapper;

import com.uam.tecnouam.prestamos.dto.PrestamoResponse;
import com.uam.tecnouam.prestamos.model.Prestamo;
import org.springframework.stereotype.Component;

@Component
public class PrestamoMapper {

    private final EquipoMapper equipoMapper;

    public PrestamoMapper(EquipoMapper equipoMapper) {
        this.equipoMapper = equipoMapper;
    }

    public PrestamoResponse toResponse(Prestamo prestamo) {
        return new PrestamoResponse(prestamo.getId(), equipoMapper.toResponse(prestamo.getEquipo()),
                prestamo.getNombreSolicitante(), prestamo.getFechaPrestamo(),
                prestamo.getFechaPrevistaDevolucion(), prestamo.getFechaDevolucionReal(), prestamo.getEstado());
    }
}
