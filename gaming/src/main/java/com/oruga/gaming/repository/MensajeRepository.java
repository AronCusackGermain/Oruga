package com.oruga.gaming.repository;

import com.oruga.gaming.entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    
    List<Mensaje> findByEsGrupalTrueOrderByFechaEnvioDesc();
    
    List<Mensaje> findByConversacionIdOrderByFechaEnvioAsc(Long conversacionId);
    
    @Query("SELECT m FROM Mensaje m WHERE m.destinatarioId = :usuarioId AND m.leido = false")
    List<Mensaje> findMensajesNoLeidos(@Param("usuarioId") Long usuarioId);
    
    Long countByDestinatarioIdAndLeidoFalse(Long destinatarioId);
}
