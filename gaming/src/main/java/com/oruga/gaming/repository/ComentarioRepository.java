package com.oruga.gaming.repository;

import com.oruga.gaming.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    
    List<Comentario> findByPublicacionIdOrderByFechaComentarioDesc(Long publicacionId);
    
    Long countByPublicacionId(Long publicacionId);
    
    void deleteByPublicacionId(Long publicacionId);
}
