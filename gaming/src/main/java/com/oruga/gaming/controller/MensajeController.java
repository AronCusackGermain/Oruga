package com.oruga.gaming.controller;

import com.oruga.gaming.dto.*;
import com.oruga.gaming.service.MensajeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
@CrossOrigin(origins = "*")
public class MensajeController {

    private final MensajeService mensajeService;

    public MensajeController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    /**
     * Enviar mensaje grupal
     */
    @PostMapping("/grupal")
    public ResponseEntity<ApiResponse<MensajeDto>> enviarMensajeGrupal(
            @Valid @RequestBody MensajeRequest request,
            Authentication authentication) {
        
        try {
            String email = authentication.getName();
            MensajeDto mensaje = mensajeService.enviarMensajeGrupal(
                    email, 
                    request.getContenido(), 
                    request.getImagenUrl()
            );
            
            return ResponseEntity.ok(
                    ApiResponse.success(mensaje, "Mensaje enviado al grupo")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error al enviar mensaje: " + e.getMessage()));
        }
    }

    /**
     * Enviar mensaje privado
     */
    @PostMapping("/privado")
    public ResponseEntity<ApiResponse<MensajeDto>> enviarMensajePrivado(
            @Valid @RequestBody MensajePrivadoRequest request,
            Authentication authentication) {
        
        try {
            String email = authentication.getName();
            MensajeDto mensaje = mensajeService.enviarMensajePrivado(
                    email,
                    request.getDestinatarioId(),
                    request.getContenido(),
                    request.getImagenUrl()
            );
            
            return ResponseEntity.ok(
                    ApiResponse.success(mensaje, "Mensaje enviado")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error al enviar mensaje: " + e.getMessage()));
        }
    }

    /**
     * Obtener mensajes del chat grupal
     */
    @GetMapping("/grupal")
    public ResponseEntity<ApiResponse<List<MensajeDto>>> obtenerMensajesGrupales() {
        try {
            List<MensajeDto> mensajes = mensajeService.obtenerMensajesGrupales();
            return ResponseEntity.ok(
                    ApiResponse.success(mensajes, "Mensajes grupales obtenidos")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtener mensajes privados con otro usuario
     */
    @GetMapping("/privado/{otroUsuarioId}")
    public ResponseEntity<ApiResponse<List<MensajeDto>>> obtenerMensajesPrivados(
            @PathVariable Long otroUsuarioId,
            Authentication authentication) {
        
        try {
            String email = authentication.getName();
            List<MensajeDto> mensajes = mensajeService.obtenerMensajesPrivados(
                    email, otroUsuarioId);
            
            return ResponseEntity.ok(
                    ApiResponse.success(mensajes, "Mensajes obtenidos")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtener lista de conversaciones
     */
    @GetMapping("/conversaciones")
    public ResponseEntity<ApiResponse<List<ConversacionDto>>> obtenerConversaciones(
            Authentication authentication) {
        
        try {
            String email = authentication.getName();
            List<ConversacionDto> conversaciones = mensajeService.obtenerConversaciones(email);
            
            return ResponseEntity.ok(
                    ApiResponse.success(conversaciones, "Conversaciones obtenidas")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Marcar mensajes como leídos
     */
    @PutMapping("/marcar-leidos")
    public ResponseEntity<ApiResponse<Void>> marcarComoLeidos(
            Authentication authentication) {
        
        try {
            String email = authentication.getName();
            mensajeService.marcarComoLeidos(email);
            
            return ResponseEntity.ok(
                    ApiResponse.success(null, "Mensajes marcados como leídos")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Contar mensajes no leídos
     */
    @GetMapping("/no-leidos")
    public ResponseEntity<ApiResponse<Long>> contarNoLeidos(
            Authentication authentication) {
        
        try {
            String email = authentication.getName();
            Long cantidad = mensajeService.contarNoLeidos(email);
            
            return ResponseEntity.ok(
                    ApiResponse.success(cantidad, "Mensajes no leídos contados")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Eliminar mensaje (solo remitente o moderador)
     */
    @DeleteMapping("/{mensajeId}")
    public ResponseEntity<ApiResponse<Void>> eliminarMensaje(
            @PathVariable Long mensajeId,
            Authentication authentication) {
        
        try {
            String email = authentication.getName();
            mensajeService.eliminarMensaje(email, mensajeId);
            
            return ResponseEntity.ok(
                    ApiResponse.success(null, "Mensaje eliminado")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }
}
