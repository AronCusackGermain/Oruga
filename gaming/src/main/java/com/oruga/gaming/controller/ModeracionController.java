package com.oruga.gaming.controller;

import com.oruga.gaming.dto.response.ApiResponse;
import com.oruga.gaming.dto.response.OrdenResponse;
import com.oruga.gaming.service.OrdenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller para funciones de moderación
 * Requiere rol de MODERADOR
 */
@RestController
@RequestMapping("/api/moderacion")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ModeracionController {

    private final OrdenService ordenService;

    /**
     * GET /api/moderacion/ordenes-pendientes
     * Obtiene todas las órdenes pendientes de revisión
     * Solo para moderadores
     */
    @GetMapping("/ordenes-pendientes")
    public ResponseEntity<ApiResponse<List<OrdenResponse>>> obtenerOrdenesPendientes() {
        List<OrdenResponse> ordenes = ordenService.obtenerOrdenesPendientesRevision();
        return ResponseEntity.ok(
                ApiResponse.success("Órdenes pendientes obtenidas exitosamente", ordenes)
        );
    }

    /**
     * POST /api/moderacion/ordenes/{id}/revisar
     * Aprueba o rechaza una orden
     * Solo para moderadores
     *
     * Body: {
     *   "aprobar": true/false,
     *   "comentario": "Razón de aprobación/rechazo"
     * }
     */
    @PostMapping("/ordenes/{id}/revisar")
    public ResponseEntity<ApiResponse<OrdenResponse>> revisarOrden(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {

        Long moderadorId = (Long) httpRequest.getAttribute("usuarioId");
        Boolean aprobar = (Boolean) request.get("aprobar");
        String comentario = (String) request.get("comentario");

        OrdenResponse orden;

        if (aprobar) {
            orden = ordenService.aprobarOrden(id, moderadorId, comentario);
        } else {
            orden = ordenService.rechazarOrden(id, moderadorId, comentario);
        }

        String mensaje = aprobar ? "Orden aprobada exitosamente" : "Orden rechazada";
        return ResponseEntity.ok(
                ApiResponse.success(mensaje, orden)
        );
    }

    /**
     * POST /api/moderacion/ordenes/{id}/aprobar
     * Aprueba una orden (forma alternativa más simple)
     * Solo para moderadores
     */
    @PostMapping("/ordenes/{id}/aprobar")
    public ResponseEntity<ApiResponse<OrdenResponse>> aprobarOrden(
            @PathVariable Long id,
            @RequestParam(value = "comentario", defaultValue = "Orden aprobada") String comentario,
            HttpServletRequest request) {

        Long moderadorId = (Long) request.getAttribute("usuarioId");

        OrdenResponse orden = ordenService.aprobarOrden(id, moderadorId, comentario);
        return ResponseEntity.ok(
                ApiResponse.success("Orden aprobada exitosamente", orden)
        );
    }

    /**
     * POST /api/moderacion/ordenes/{id}/rechazar
     * Rechaza una orden (forma alternativa más simple)
     * Solo para moderadores
     */
    @PostMapping("/ordenes/{id}/rechazar")
    public ResponseEntity<ApiResponse<OrdenResponse>> rechazarOrden(
            @PathVariable Long id,
            @RequestParam(value = "comentario", defaultValue = "Orden rechazada") String comentario,
            HttpServletRequest request) {

        Long moderadorId = (Long) request.getAttribute("usuarioId");

        OrdenResponse orden = ordenService.rechazarOrden(id, moderadorId, comentario);
        return ResponseEntity.ok(
                ApiResponse.success("Orden rechazada", orden)
        );
    }
}