package com.oruga.backend.repository;

import com.oruga.backend.model.Mensaje;
import com.oruga.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByEsGrupalOrderByFechaEnvioAsc(Boolean esGrupal);

    @Query("SELECT m FROM Mensaje m WHERE m.esGrupal = false " +
            "AND ((m.remitente.id = :usuarioId AND m.destinatario.id = :otroUsuarioId) " +
            "OR (m.remitente.id = :otroUsuarioId AND m.destinatario.id = :usuarioId)) " +
            "ORDER BY m.fechaEnvio ASC")
    List<Mensaje> findMensajesPrivados(@Param("usuarioId") Long usuarioId,
                                       @Param("otroUsuarioId") Long otroUsuarioId);

    Long countByDestinatarioAndLeidoFalse(Usuario destinatario);
}