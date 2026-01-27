package com.oruga.backend.repository;

import com.oruga.backend.model.Publicacion;
import com.oruga.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
    List<Publicacion> findByAutorOrderByFechaPublicacionDesc(Usuario autor);
    List<Publicacion> findByEsAnuncioOrderByFechaPublicacionDesc(Boolean esAnuncio);
    List<Publicacion> findAllByOrderByFechaPublicacionDesc();
}