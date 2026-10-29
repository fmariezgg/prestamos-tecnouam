package com.uam.tecnouam.prestamos.mapper;

import com.uam.tecnouam.prestamos.dto.EquipoRequest;
import com.uam.tecnouam.prestamos.dto.EquipoResponse;
import com.uam.tecnouam.prestamos.model.Equipo;
import org.springframework.stereotype.Component;

@Component
public class EquipoMapper {

    public Equipo toEntity(EquipoRequest request) {
        return new Equipo(request.codigoPatrimonial().trim(), request.nombre().trim(), request.tipo().trim());
    }

    public void updateEntity(Equipo equipo, EquipoRequest request) {
        equipo.setCodigoPatrimonial(request.codigoPatrimonial().trim());
        equipo.setNombre(request.nombre().trim());
        equipo.setTipo(request.tipo().trim());
    }

    public EquipoResponse toResponse(Equipo equipo) {
        return new EquipoResponse(equipo.getId(), equipo.getCodigoPatrimonial(), equipo.getNombre(),
                equipo.getTipo(), equipo.getEstado());
    }
}
