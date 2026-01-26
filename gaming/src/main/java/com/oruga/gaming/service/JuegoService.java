package com.oruga.gaming.service;

import com.oruga.gaming.dto.response.JuegoResponse;
import com.oruga.gaming.entity.Juego;
import com.oruga.gaming.exception.ResourceNotFoundException;
import com.oruga.gaming.repository.JuegoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión del catálogo de juegos
 */
@Service
@RequiredArgsConstructor
public class JuegoService {

    private final JuegoRepository juegoRepository;

    /**
     * Obtiene todos los juegos activos
     */
    public List<JuegoResponse> obtenerTodosLosJuegos() {
        return juegoRepository.findByActivoTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un juego por ID
     */
    public JuegoResponse obtenerJuegoPorId(Long id) {
        Juego juego = juegoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Juego", "id", id));
        return mapToResponse(juego);
    }

    /**
     * Busca juegos por género
     */
    public List<JuegoResponse> buscarPorGenero(String genero) {
        return juegoRepository.findByGeneroContaining(genero).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Verifica disponibilidad de stock
     */
    public boolean verificarStock(Long juegoId, int cantidad) {
        Juego juego = juegoRepository.findById(juegoId)
                .orElseThrow(() -> new ResourceNotFoundException("Juego", "id", juegoId));
        return juego.getStock() >= cantidad;
    }

    /**
     * Reduce el stock después de una compra
     */
    @Transactional
    public void reducirStock(Long juegoId, int cantidad) {
        Juego juego = juegoRepository.findById(juegoId)
                .orElseThrow(() -> new ResourceNotFoundException("Juego", "id", juegoId));

        juego.reducirStock(cantidad);
        juegoRepository.save(juego);
    }

    /**
     * Convierte Juego a JuegoResponse
     */
    private JuegoResponse mapToResponse(Juego juego) {
        return JuegoResponse.builder()
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