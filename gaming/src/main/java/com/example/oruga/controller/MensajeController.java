package com.example.oruga.controller;

import com.example.oruga.model.Mensaje;
import com.example.oruga.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para Mensaje
 * Base URL: /api/mensajes
 */
@RestController
@RequestMapping("/api/mensajes")
@CrossOrigin(origins = "*")
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    /**
     * POST /api/mensajes/grupal
     * Enviar mensaje grupal
     */
    @PostMapping("/grupal")
    public ResponseEntity<?> enviarMensajeGrupal(@Valid @RequestBody Mensaje mensaje) {
        try {
            Mensaje nuevo = mensajeService.enviarMensajeGrupal(mensaje);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Mensaje enviado");
            response.put("mensaje", nuevo);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * POST /api/mensajes/privado
     * Enviar mensaje privado
     */
    @PostMapping("/privado")
    public ResponseEntity<?> enviarMensajePrivado(@Valid @RequestBody Mensaje mensaje) {
        try {
            Mensaje nuevo = mensajeService.enviarMensajePrivado(mensaje);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Mensaje enviado");
            response.put("mensaje", nuevo);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/mensajes/grupal
     * Obtener mensajes grupales
     */
    @GetMapping("/grupal")
    public ResponseEntity<List<Mensaje>> obtenerMensajesGrupales() {
        List<Mensaje> mensajes = mensajeService.obtenerMensajesGrupales();
        return ResponseEntity.ok(mensajes);
    }

    /**
     * GET /api/mensajes/privado/{usuarioId}/{otroUsuarioId}
     * Obtener mensajes privados entre dos usuarios
     */
    @GetMapping("/privado/{usuarioId}/{otroUsuarioId}")
    public ResponseEntity<List<Mensaje>> obtenerMensajesPrivados(
            @PathVariable Integer usuarioId,
            @PathVariable Integer otroUsuarioId) {
        List<Mensaje> mensajes = mensajeService.obtenerMensajesPrivados(usuarioId, otroUsuarioId);
        return ResponseEntity.ok(mensajes);
    }

    /**
     * GET /api/mensajes/no-leidos/{usuarioId}
     * Contar mensajes no leídos
     */
    @GetMapping("/no-leidos/{usuarioId}")
    public ResponseEntity<?> contarNoLeidos(@PathVariable Integer usuarioId) {
        Integer count = mensajeService.contarNoLeidos(usuarioId);

        Map<String, Object> response = new HashMap<>();
        response.put("usuarioId", usuarioId);
        response.put("mensajesNoLeidos", count);

        return ResponseEntity.ok(response);
    }
}