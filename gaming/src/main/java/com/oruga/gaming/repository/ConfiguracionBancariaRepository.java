package com.oruga.gaming.repository;

import com.oruga.gaming.entity.ConfiguracionBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionBancariaRepository extends JpaRepository<ConfiguracionBancaria, Long> {
    
    Optional<ConfiguracionBancaria> findFirstByActivoTrue();
}
