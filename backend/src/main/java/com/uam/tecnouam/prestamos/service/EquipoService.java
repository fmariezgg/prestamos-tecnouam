package com.uam.tecnouam.prestamos.service;

import com.uam.tecnouam.prestamos.dto.EquipoRequest;
import com.uam.tecnouam.prestamos.dto.EquipoResponse;
import com.uam.tecnouam.prestamos.exception.ConflictoNegocioException;
import com.uam.tecnouam.prestamos.exception.RecursoNoEncontradoException;
import com.uam.tecnouam.prestamos.mapper.EquipoMapper;
import com.uam.tecnouam.prestamos.model.Equipo;
import com.uam.tecnouam.prestamos.model.EstadoPrestamo;
import com.uam.tecnouam.prestamos.repository.EquipoRepository;
import com.uam.tecnouam.prestamos.repository.PrestamoRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final PrestamoRepository prestamoRepository;
    private final EquipoMapper mapper;

    public EquipoService(EquipoRepository equipoRepository, PrestamoRepository prestamoRepository,
                         EquipoMapper mapper) {
        this.equipoRepository = equipoRepository;
        this.prestamoRepository = prestamoRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<EquipoResponse> listar() {
        return equipoRepository.findAll(Sort.by("id")).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EquipoResponse obtener(Long id) {
        return mapper.toResponse(buscar(id));
    }

    @Transactional
    public EquipoResponse crear(EquipoRequest request) {
        String codigo = request.codigoPatrimonial().trim();
        if (equipoRepository.existsByCodigoPatrimonial(codigo)) {
            throw codigoDuplicado(codigo);
        }
        return mapper.toResponse(equipoRepository.saveAndFlush(mapper.toEntity(request)));
    }

    @Transactional
    public EquipoResponse actualizar(Long id, EquipoRequest request) {
        Equipo equipo = buscar(id);
        String codigo = request.codigoPatrimonial().trim();
        if (equipoRepository.existsByCodigoPatrimonialAndIdNot(codigo, id)) {
            throw codigoDuplicado(codigo);
        }
        mapper.updateEntity(equipo, request);
        return mapper.toResponse(equipoRepository.saveAndFlush(equipo));
    }

    /**
     * An equipo with an ACTIVO loan cannot be deleted (409). When it only has DEVUELTO loans,
     * that returned history is deleted together with the equipo (explicit cascade of history,
     * so the foreign key never blocks the delete and no orphan loans remain).
     */
    @Transactional
    public void eliminar(Long id) {
        Equipo equipo = buscar(id);
        if (prestamoRepository.existsByEquipoIdAndEstado(id, EstadoPrestamo.ACTIVO)) {
            throw new ConflictoNegocioException("No se puede eliminar el equipo: tiene préstamos activos");
        }
        prestamoRepository.deleteByEquipoId(id);
        equipoRepository.delete(equipo);
    }

    private Equipo buscar(Long id) {
        return equipoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Equipo con id " + id + " no encontrado"));
    }

    private ConflictoNegocioException codigoDuplicado(String codigo) {
        return new ConflictoNegocioException("Ya existe un equipo con el código patrimonial " + codigo);
    }
}
