package com.oruga.gaming.controller;

import com.oruga.gaming.dto.response.ApiResponse;
import com.oruga.gaming.dto.response.OrdenResponse;
import com.oruga.gaming.entity.ConfiguracionBancaria;
import com.oruga.gaming.repository.ConfiguracionBancariaRepository;
import com.oruga.gaming.service.FileStorageService;
import com.oruga.gaming.service.OrdenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller para proceso de checkout y órdenes
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CheckoutController {

    private final OrdenService ordenService;
    private final FileStorageService fileStorageService;
    private final ConfiguracionBancariaRepository configuracionBancariaRepository;

    /**
     * POST /api/checkout/iniciar
     * Inicia el proceso de checkout
     * Crea una orden desde el carrito y devuelve los datos bancarios
     */
    @PostMapping("/checkout/iniciar")
    public ResponseEntity<ApiResponse<Map<String, Object>>> iniciarCheckout(
            HttpServletRequest request) {

        Long usuarioId = (Long) request.getAttribute("usuarioId");
        Boolean esModerador = (Boolean) request.getAttribute("esModerador");

        // Crear orden
        OrdenResponse orden = ordenService.iniciarCheckout(usuarioId, esModerador);

        // Obtener configuración bancaria
        ConfiguracionBancaria config = configuracionBancariaRepository.findFirstByActivoTrue()
                .orElseThrow(() -> new RuntimeException("Configuración bancaria no disponible"));

        // Preparar respuesta con orden + datos bancarios
        Map<String, Object> response = new HashMap<>();
        response.put("orden", orden);
        response.put("datosBancarios", Map.of(
                "nombreTitular", config.getNombreTitular(),
                "banco", config.getBanco(),
                "tipoCuenta", config.getTipoCuenta(),
                "numeroCuenta", config.getNumeroCuenta(),
                "rutTitular", config.getRutTitular(),
                "emailConfirmacion", config.getEmailConfirmacion()
        ));

        return ResponseEntity.ok(
                ApiResponse.success("Checkout iniciado exitosamente", response)
        );
    }

    /**
     * POST /api/ordenes/{id}/comprobante
     * Sube el comprobante de pago de una orden
     * Content-Type: multipart/form-data
     */
    @PostMapping(value = "/ordenes/{id}/comprobante", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<OrdenResponse>> subirComprobante(
            @PathVariable Long id,
            @RequestParam("comprobante") MultipartFile comprobante) {

        // Guardar comprobante
        String comprobanteUrl = fileStorageService.guardarComprobantePago(comprobante);

        // Actualizar orden
        OrdenResponse orden = ordenService.subirComprobante(id, comprobanteUrl);

        return ResponseEntity.ok(
                ApiResponse.success("Comprobante subido exitosamente. Tu orden está en revisión.", orden)
        );
    }

    /**
     * GET /api/ordenes
     * Obtiene el historial de órdenes del usuario
     */
    @GetMapping("/ordenes")
    public ResponseEntity<ApiResponse<List<OrdenResponse>>> obtenerOrdenesPorUsuario(
            HttpServletRequest request) {

        Long usuarioId = (Long) request.getAttribute("usuarioId");

        List<OrdenResponse> ordenes = ordenService.obtenerOrdenesPorUsuario(usuarioId);
        return ResponseEntity.ok(
                ApiResponse.success("Órdenes obtenidas exitosamente", ordenes)
        );
    }

    /**
     * GET /api/ordenes/{id}
     * Obtiene el detalle de una orden específica
     */
    @GetMapping("/ordenes/{id}")
    public ResponseEntity<ApiResponse<OrdenResponse>> obtenerOrdenPorId(@PathVariable Long id) {
        OrdenResponse orden = ordenService.obtenerOrdenPorId(id);
        return ResponseEntity.ok(
                ApiResponse.success("Orden obtenida exitosamente", orden)
        );
    }
}