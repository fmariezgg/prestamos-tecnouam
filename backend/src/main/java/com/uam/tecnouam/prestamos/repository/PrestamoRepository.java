package com.uam.tecnouam.prestamos.repository;

import com.uam.tecnouam.prestamos.model.EstadoPrestamo;
import com.uam.tecnouam.prestamos.model.Prestamo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    @Query("select p from Prestamo p join fetch p.equipo order by p.id desc")
    List<Prestamo> findAllWithEquipo();

    boolean existsByEquipoIdAndEstado(Long equipoId, EstadoPrestamo estado);

    // Bulk delete bypasses the persistence context, so flush first and clear stale entities afterwards.
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Prestamo p where p.equipo.id = :equipoId")
    void deleteByEquipoId(@Param("equipoId") Long equipoId);
}
