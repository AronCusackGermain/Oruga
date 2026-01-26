package com.oruga.gaming.repository;

import com.oruga.gaming.entity.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    
    List<CarritoItem> findByCarritoId(Long carritoId);
    
    Optional<CarritoItem> findByCarritoIdAndJuegoId(Long carritoId, Long juegoId);
    
    void deleteByCarritoId(Long carritoId);
    
    Long countByCarritoId(Long carritoId);
}
