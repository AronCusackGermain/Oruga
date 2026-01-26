package com.oruga.gaming.repository;

import com.oruga.gaming.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    
    Optional<Orden> findByNumeroOrden(String numeroOrden);
    
    List<Orden> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
    
    List<Orden> findByEstadoOrderByFechaCreacionDesc(String estado);
}
