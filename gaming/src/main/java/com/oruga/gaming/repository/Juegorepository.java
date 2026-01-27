package com.oruga.gaming.repository;

import com.oruga.gaming.entity.Juego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Juegorepository extends JpaRepository<Juego, Long> {

    List<Juego> findByActivoTrue();

    List<Juego> findByGeneroContainingIgnoreCaseAndActivoTrue(String genero);

    List<Juego> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);

    List<Juego> findByStockGreaterThan(Integer stock);
}