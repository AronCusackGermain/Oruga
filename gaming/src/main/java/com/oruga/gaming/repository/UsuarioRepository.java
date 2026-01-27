package com.oruga.gaming.repository;

import com.oruga.gaming.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Boolean existsByEmail(String email);

    List<Usuario> findByEstadoConexion(Boolean estadoConexion);

    List<Usuario> findByEstaBaneado(Boolean estaBaneado);

    List<Usuario> findByEsModerador(Boolean esModerador);
}