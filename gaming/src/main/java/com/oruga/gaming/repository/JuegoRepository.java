package com.oruga.gaming.repository;

import com.oruga.gaming.entity.Juego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JuegoRepository extends JpaRepository<Juego, Long> {
    
    List<Juego> findByActivoTrue();
    
    List<Juego> findByGeneroContaining(String genero);
    
    List<Juego> findByStockGreaterThan(int stock);
}
