package com.example.oruga.repository;

import com.example.oruga.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para Mensaje
 */
@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {

    List<Mensaje> findByEsGrupalOrderByFechaEnvioAsc(Boolean esGrupal);

    @Query("SELECT m FROM Mensaje m WHERE m.esGrupal = false " +
            "AND ((m.remitenteId = :usuarioId AND m.destinatarioId = :otroUsuarioId) " +
            "OR (m.remitenteId = :otroUsuarioId AND m.destinatarioId = :usuarioId)) " +
            "ORDER BY m.fechaEnvio ASC")
    List<Mensaje> findMensajesPrivados(Integer usuarioId, Integer otroUsuarioId);

    Integer countByDestinatarioIdAndLeidoFalse(Integer destinatarioId);
}