package com.oruga.gaming.controller;

import com.oruga.gaming.dto.request.AgregarCarritoRequest;
import com.oruga.gaming.dto.response.ApiResponse;
import com.oruga.gaming.dto.response.CarritoResponse;
import com.oruga.gaming.service.CarritoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para carrito de compras
 */
@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CarritoController {

    private final CarritoService carritoService;

    /**
     * GET /api/carrito
     * Obtiene el carrito del usuario con todos los detalles
     * Aplica descuento del 15% si es moderador
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CarritoResponse>> obtenerCarrito(HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");
        Boolean esModerador = (Boolean) request.getAttribute("esModerador");

        CarritoResponse carrito = carritoService.obtenerCarrito(usuarioId, esModerador);
        return ResponseEntity.ok(
                ApiResponse.success("Carrito obtenido exitosamente", carrito)
        );
    }

    /**
     * POST /api/carrito/agregar
     * Agrega un juego al carrito
     */
    @PostMapping("/agregar")
    public ResponseEntity<ApiResponse<CarritoResponse>> agregarAlCarrito(
            @Valid @RequestBody AgregarCarritoRequest request,
            HttpServletRequest httpRequest) {

        Long usuarioId = (Long) httpRequest.getAttribute("usuarioId");
        Boolean esModerador = (Boolean) httpRequest.getAttribute("esModerador");

        CarritoResponse carrito = carritoService.agregarAlCarrito(usuarioId, esModerador, request);
        return ResponseEntity.ok(
                ApiResponse.success("Juego agregado al carrito exitosamente", carrito)
        );
    }

    /**
     * PUT /api/carrito/item/{juegoId}
     * Actualiza la cantidad de un item en el carrito
     */
    @PutMapping("/item/{juegoId}")
    public ResponseEntity<ApiResponse<Void>> actualizarCantidad(
            @PathVariable Long juegoId,
            @RequestParam Integer cantidad,
            HttpServletRequest request) {

        Long usuarioId = (Long) request.getAttribute("usuarioId");

        carritoService.actualizarCantidad(usuarioId, juegoId, cantidad);
        return ResponseEntity.ok(
                ApiResponse.success("Cantidad actualizada exitosamente", null)
        );
    }

    /**
     * DELETE /api/carrito/item/{juegoId}
     * Elimina un juego del carrito
     */
    @DeleteMapping("/item/{juegoId}")
    public ResponseEntity<ApiResponse<Void>> eliminarItem(
            @PathVariable Long juegoId,
            HttpServletRequest request) {

        Long usuarioId = (Long) request.getAttribute("usuarioId");

        carritoService.eliminarItem(usuarioId, juegoId);
        return ResponseEntity.ok(
                ApiResponse.success("Item eliminado del carrito exitosamente", null)
        );
    }

    /**
     * DELETE /api/carrito/limpiar
     * Vacía todo el carrito
     */
    @DeleteMapping("/limpiar")
    public ResponseEntity<ApiResponse<Void>> vaciarCarrito(HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");

        carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.ok(
                ApiResponse.success("Carrito vaciado exitosamente", null)
        );
    }
}