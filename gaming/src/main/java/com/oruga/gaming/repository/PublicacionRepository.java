package com.oruga.gaming.repository;

import com.oruga.gaming.entity.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
    
    List<Publicacion> findByEsAnuncioTrueOrderByFechaCreacionDesc();
    
    List<Publicacion> findByAutorIdOrderByFechaCreacionDesc(Long autorId);
    
    List<Publicacion> findAllByOrderByFechaCreacionDesc();
}
