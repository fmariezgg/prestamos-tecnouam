package com.uam.tecnouam.prestamos.service;

import com.uam.tecnouam.prestamos.dto.PrestamoRequest;
import com.uam.tecnouam.prestamos.dto.PrestamoResponse;
import com.uam.tecnouam.prestamos.exception.ConflictoNegocioException;
import com.uam.tecnouam.prestamos.exception.RecursoNoEncontradoException;
import com.uam.tecnouam.prestamos.mapper.PrestamoMapper;
import com.uam.tecnouam.prestamos.model.Equipo;
import com.uam.tecnouam.prestamos.model.EstadoEquipo;
import com.uam.tecnouam.prestamos.model.Prestamo;
import com.uam.tecnouam.prestamos.repository.EquipoRepository;
import com.uam.tecnouam.prestamos.repository.PrestamoRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final EquipoRepository equipoRepository;
    private final PrestamoMapper mapper;
    private final Clock clock = Clock.systemDefaultZone();

    public PrestamoService(PrestamoRepository prestamoRepository, EquipoRepository equipoRepository,
                           PrestamoMapper mapper) {
        this.prestamoRepository = prestamoRepository;
        this.equipoRepository = equipoRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listar() {
        return prestamoRepository.findAllWithEquipo().stream().map(mapper::toResponse).toList();
    }

    /** Creates the loan and flips the equipo to PRESTADO in the same transaction. */
    @Transactional
    public PrestamoResponse registrar(PrestamoRequest request) {
        Equipo equipo = equipoRepository.findByIdForUpdate(request.equipoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Equipo con id " + request.equipoId() + " no encontrado"));
        if (!equipo.estaDisponible()) {
            throw new ConflictoNegocioException("El equipo " + equipo.getCodigoPatrimonial() + " ya está prestado");
        }
        equipo.setEstado(EstadoEquipo.PRESTADO);
        Prestamo prestamo = new Prestamo(equipo, request.nombreSolicitante().trim(), request.fechaPrestamo(),
                request.fechaPrevistaDevolucion());
        return mapper.toResponse(prestamoRepository.save(prestamo));
    }

    /** Marks the loan as DEVUELTO and frees the equipo in the same transaction. */
    @Transactional
    public PrestamoResponse devolver(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo con id " + id + " no encontrado"));
        if (!prestamo.estaActivo()) {
            throw new ConflictoNegocioException("El préstamo " + id + " ya fue devuelto");
        }
        prestamo.marcarDevuelto(LocalDate.now(clock));
        prestamo.getEquipo().setEstado(EstadoEquipo.DISPONIBLE);
        return mapper.toResponse(prestamo);
    }
}
