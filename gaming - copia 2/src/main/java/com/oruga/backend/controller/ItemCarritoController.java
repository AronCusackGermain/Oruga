package com.oruga.backend.controller;

import com.oruga.backend.dto.request.ActualizarCantidadRequest;
import com.oruga.backend.dto.request.AgregarCarritoRequest;
import com.oruga.backend.dto.response.CompraResponse;
import com.oruga.backend.dto.response.ItemCarritoResponse;
import com.oruga.backend.service.ItemCarritoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión del Carrito de Compras
 */
@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ItemCarritoController {

    private final ItemCarritoService itemCarritoService;

    /**
     * Agregar item al carrito
     * POST /api/carrito
     */
    @PostMapping
    public ResponseEntity<ItemCarritoResponse> agregar(
            @Valid @RequestBody AgregarCarritoRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(itemCarritoService.agregarAlCarrito(request, auth));
    }

    /**
     * Obtener carrito del usuario
     * GET /api/carrito
     */
    @GetMapping
    public ResponseEntity<List<ItemCarritoResponse>> obtenerCarrito(Authentication auth) {
        return ResponseEntity.ok(itemCarritoService.obtenerCarrito(auth));
    }

    /**
     * Actualizar cantidad de un item
     * PUT /api/carrito/{itemId}
     */
    @PutMapping("/{itemId}")
    public ResponseEntity<ItemCarritoResponse> actualizarCantidad(
            @PathVariable Long itemId,
            @Valid @RequestBody ActualizarCantidadRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(itemCarritoService.actualizarCantidad(itemId, request, auth));
    }

    /**
     * Eliminar un item del carrito
     * DELETE /api/carrito/{itemId}
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long itemId, Authentication auth) {
        itemCarritoService.eliminarItem(itemId, auth);
        return ResponseEntity.ok().build();
    }

    /**
     * Vaciar todo el carrito
     * DELETE /api/carrito/vaciar
     */
    @DeleteMapping("/vaciar")
    public ResponseEntity<Void> vaciarCarrito(Authentication auth) {
        itemCarritoService.vaciarCarrito(auth);
        return ResponseEntity.ok().build();
    }

    /**
     * Contar items en el carrito
     * GET /api/carrito/cantidad
     */
    @GetMapping("/cantidad")
    public ResponseEntity<Long> contarItems(Authentication auth) {
        return ResponseEntity.ok(itemCarritoService.contarItems(auth));
    }

    /**
     * Calcular total del carrito
     * GET /api/carrito/total
     */
    @GetMapping("/total")
    public ResponseEntity<Double> calcularTotal(Authentication auth) {
        return ResponseEntity.ok(itemCarritoService.calcularTotal(auth));
    }

    /**
     * Procesar compra
     * POST /api/carrito/comprar
     */
    @PostMapping("/comprar")
    public ResponseEntity<CompraResponse> procesarCompra(Authentication auth) {
        return ResponseEntity.ok(itemCarritoService.procesarCompra(auth));
    }
}
