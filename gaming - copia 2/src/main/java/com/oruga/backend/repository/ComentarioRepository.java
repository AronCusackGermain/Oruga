package com.oruga.backend.repository;

import com.oruga.backend.model.Comentario;
import com.oruga.backend.model.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByPublicacionOrderByFechaComentarioAsc(Publicacion publicacion);
    Long countByPublicacion(Publicacion publicacion);
}