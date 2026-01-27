package com.example.oruga.repository;

import com.example.oruga.model.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repositorio para Publicacion
 */
@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Integer> {

    List<Publicacion> findByEsAnuncio(Boolean esAnuncio);

    List<Publicacion> findByAutorIdOrderByFechaPublicacionDesc(Integer autorId);

    List<Publicacion> findAllByOrderByFechaPublicacionDesc();

    @Transactional
    @Modifying
    @Query("UPDATE Publicacion p SET p.cantidadLikes = p.cantidadLikes + 1 WHERE p.id = :publicacionId")
    void incrementarLikes(Integer publicacionId);

    @Transactional
    @Modifying
    @Query("UPDATE Publicacion p SET p.cantidadComentarios = p.cantidadComentarios + 1 WHERE p.id = :publicacionId")
    void incrementarComentarios(Integer publicacionId);
}