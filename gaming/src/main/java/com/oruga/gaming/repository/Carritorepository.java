package com.oruga.gaming.repository;

import com.oruga.gaming.entity.Carrito;
import com.oruga.gaming.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Carritorepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuario(Usuario usuario);

    Optional<Carrito> findByUsuarioId(Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}