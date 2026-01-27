package com.oruga.backend.repository;

import com.oruga.backend.model.ItemCarrito;
import com.oruga.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para gestionar Items del Carrito
 */
@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    /**
     * Buscar todos los items del carrito de un usuario
     */
    List<ItemCarrito> findByUsuarioOrderByFechaAgregadoDesc(Usuario usuario);

    /**
     * Buscar un item específico por usuario y juego
     */
    Optional<ItemCarrito> findByUsuarioAndJuegoId(Usuario usuario, Long juegoId);

    /**
     * Contar items en el carrito de un usuario
     */
    Long countByUsuario(Usuario usuario);

    /**
     * Eliminar todos los items del carrito de un usuario
     */
    void deleteAllByUsuario(Usuario usuario);

    /**
     * Verificar si existe un juego en el carrito del usuario
     */
    boolean existsByUsuarioAndJuegoId(Usuario usuario, Long juegoId);
}