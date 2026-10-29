package com.uam.tecnouam.prestamos.repository;

import com.uam.tecnouam.prestamos.model.Equipo;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    boolean existsByCodigoPatrimonial(String codigoPatrimonial);

    boolean existsByCodigoPatrimonialAndIdNot(String codigoPatrimonial, Long id);

    // Row lock so two concurrent loans of the same equipo cannot both succeed.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Equipo e where e.id = :id")
    Optional<Equipo> findByIdForUpdate(@Param("id") Long id);
}
