package com.oruga.gaming.repository;

import com.oruga.gaming.entity.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
    
    List<Publicacion> findByAutorIdOrderByFechaPublicacionDesc(Long autorId);
    
    List<Publicacion> findByEsAnuncioTrueOrderByFechaPublicacionDesc();
    
    List<Publicacion> findAllByOrderByFechaPublicacionDesc();
}
