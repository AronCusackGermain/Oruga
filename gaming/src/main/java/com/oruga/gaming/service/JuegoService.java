package com.oruga.gaming.service;

import com.oruga.gaming.dto.JuegoDto;
import com.oruga.gaming.entity.Juego;
import com.oruga.gaming.repository.Juegorepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class JuegoService {

    private final Juegorepository juegoRepository;

    public JuegoService(Juegorepository juegoRepository) {
        this.juegoRepository = juegoRepository;
    }

    public List<com.oruga.gaming.dto.JuegoDto> obtenerTodos() {
        return juegoRepository.findByActivoTrue()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public JuegoDto obtenerPorId(Long id) {
        Juego juego = juegoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));
        return toDto(juego);
    }

    public List<JuegoDto> buscarPorGenero(String genero) {
        return juegoRepository.findByGeneroContainingIgnoreCaseAndActivoTrue(genero)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public JuegoDto crearJuego(Juego juego) {
        Juego nuevoJuego = juegoRepository.save(juego);
        return toDto(nuevoJuego);
    }

    public JuegoDto actualizarJuego(Long id, Juego juegoActualizado) {
        Juego juego = juegoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));
        
        juego.setNombre(juegoActualizado.getNombre());
        juego.setDescripcion(juegoActualizado.getDescripcion());
        juego.setGenero(juegoActualizado.getGenero());
        juego.setPrecio(juegoActualizado.getPrecio());
        juego.setStock(juegoActualizado.getStock());
        juego.setImagenUrl(juegoActualizado.getImagenUrl());
        juego.setPlataformas(juegoActualizado.getPlataformas());
        juego.setDesarrollador(juegoActualizado.getDesarrollador());
        juego.setCalificacion(juegoActualizado.getCalificacion());
        
        Juego juegoGuardado = juegoRepository.save(juego);
        return toDto(juegoGuardado);
    }

    public void actualizarStock(Long juegoId, Integer cantidad) {
        Juego juego = juegoRepository.findById(juegoId)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));
        
        int nuevoStock = juego.getStock() - cantidad;
        if (nuevoStock < 0) {
            throw new RuntimeException("Stock insuficiente");
        }
        
        juego.setStock(nuevoStock);
        juegoRepository.save(juego);
    }

    public JuegoDto toDto(Juego juego) {
        return JuegoDto.builder()
                .id(juego.getId())
                .nombre(juego.getNombre())
                .descripcion(juego.getDescripcion())
                .genero(juego.getGenero())
                .precio(juego.getPrecio())
                .precioFormateado(juego.getPrecioFormateado())
                .stock(juego.getStock())
                .imagenUrl(juego.getImagenUrl())
                .plataformas(juego.getPlataformas())
                .desarrollador(juego.getDesarrollador())
                .fechaLanzamiento(juego.getFechaLanzamiento())
                .calificacion(juego.getCalificacion())
                .activo(juego.getActivo())
                .build();
    }
}
