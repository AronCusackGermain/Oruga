package com.oruga.gaming.controller;

import com.oruga.gaming.dto.*;
import com.oruga.gaming.service.CarritoService;
import com.oruga.gaming.dto.AgregarCarritoRequest;
import com.oruga.gaming.dto.ApiResponse;
import com.oruga.gaming.dto.CarritoResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "*")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CarritoResponseDto>> obtenerCarrito(
            Authentication authentication) {

        try {
            String email = authentication.getName();
            CarritoResponseDto carrito = carritoService.obtenerCarritoPorEmail(email);

            return ResponseEntity.ok(
                    ApiResponse.success(carrito, "Carrito obtenido correctamente")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error al obtener carrito: " + e.getMessage()));
        }
    }

    @PostMapping("/agregar")
    public ResponseEntity<ApiResponse<CarritoResponseDto>> agregarAlCarrito(
            @Valid @RequestBody AgregarCarritoRequest request,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            CarritoResponseDto carrito = carritoService.agregarItem(
                    email,
                    request.getJuegoId(),
                    request.getCantidad()
            );

            return ResponseEntity.ok(
                    ApiResponse.success(carrito, "Producto agregado al carrito")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/item/{juegoId}")
    public ResponseEntity<ApiResponse<Void>> eliminarDelCarrito(
            @PathVariable Long juegoId,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            carritoService.eliminarItem(email, juegoId);

            return ResponseEntity.ok(
                    ApiResponse.success(null, "Producto eliminado del carrito")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/vaciar")
    public ResponseEntity<ApiResponse<Void>> vaciarCarrito(
            Authentication authentication) {

        try {
            String email = authentication.getName();
            carritoService.vaciarCarrito(email);

            return ResponseEntity.ok(
                    ApiResponse.success(null, "Carrito vaciado")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<String>> procesarCompra(
            Authentication authentication) {

        try {
            String email = authentication.getName();
            String resultado = carritoService.procesarCompra(email);

            return ResponseEntity.ok(
                    ApiResponse.success(resultado, "Compra procesada exitosamente")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error al procesar compra: " + e.getMessage()));
        }
    }
}