package com.example.oruga.repository;

import com.example.oruga.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Usuario - Operaciones de base de datos
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByEstadoConexion(Boolean conectado);

    List<Usuario> findByEstaBaneado(Boolean baneado);

    @Transactional
    @Modifying
    @Query("UPDATE Usuario u SET u.estadoConexion = :estado WHERE u.id = :usuarioId")
    void actualizarEstadoConexion(Integer usuarioId, Boolean estado);

    @Transactional
    @Modifying
    @Query("UPDATE Usuario u SET u.ultimaConexion = :timestamp WHERE u.id = :usuarioId")
    void actualizarUltimaConexion(Integer usuarioId, Long timestamp);

    @Transactional
    @Modifying
    @Query("UPDATE Usuario u SET u.estaBaneado = :baneado, u.fechaBaneo = :fecha, u.razonBaneo = :razon WHERE u.id = :usuarioId")
    void banearUsuario(Integer usuarioId, Boolean baneado, Long fecha, String razon);

    @Transactional
    @Modifying
    @Query("UPDATE Usuario u SET u.cantidadPublicaciones = u.cantidadPublicaciones + 1 WHERE u.id = :usuarioId")
    void incrementarPublicaciones(Integer usuarioId);

    @Transactional
    @Modifying
    @Query("UPDATE Usuario u SET u.cantidadMensajes = u.cantidadMensajes + 1 WHERE u.id = :usuarioId")
    void incrementarMensajes(Integer usuarioId);
}