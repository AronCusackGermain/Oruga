package com.oruga.backend.repository;

import com.oruga.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Usuario
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    Boolean existsByEmail(String email);

    Boolean existsByNombreUsuario(String nombreUsuario);

    List<Usuario> findByEstadoConexion(Boolean conectado);

    List<Usuario> findByEstaBaneado(Boolean baneado);

    List<Usuario> findByEsModerador(Boolean esModerador);
}