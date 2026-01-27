package com.oruga.gaming.repository;

import com.oruga.gaming.entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    
    // Mensajes grupales
    List<Mensaje> findByEsGrupalTrueOrderByFechaEnvioAsc();
    
    // Mensajes privados entre dos usuarios
    @Query("SELECT m FROM Mensaje m WHERE m.esGrupal = false " +
           "AND ((m.remitenteId = :usuarioId AND m.destinatarioId = :otroUsuarioId) " +
           "OR (m.remitenteId = :otroUsuarioId AND m.destinatarioId = :usuarioId)) " +
           "ORDER BY m.fechaEnvio ASC")
    List<Mensaje> findMensajesPrivados(@Param("usuarioId") Long usuarioId, 
                                       @Param("otroUsuarioId") Long otroUsuarioId);
    
    // Contar mensajes no leídos
    Long countByDestinatarioIdAndLeidoFalse(Long destinatarioId);
    
    // Obtener conversaciones (usuarios con los que he hablado)
    @Query("SELECT DISTINCT CASE " +
           "WHEN m.remitenteId = :usuarioId THEN m.destinatarioId " +
           "ELSE m.remitenteId END " +
           "FROM Mensaje m WHERE m.esGrupal = false " +
           "AND (m.remitenteId = :usuarioId OR m.destinatarioId = :usuarioId)")
    List<Long> findConversaciones(@Param("usuarioId") Long usuarioId);
    
    // Marcar mensajes como leídos
    @Query("UPDATE Mensaje m SET m.leido = true " +
           "WHERE m.destinatarioId = :usuarioId AND m.leido = false")
    void marcarComoLeidos(@Param("usuarioId") Long usuarioId);
}
