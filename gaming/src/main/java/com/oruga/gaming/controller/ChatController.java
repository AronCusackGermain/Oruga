package com.oruga.gaming.controller;

import com.oruga.gaming.dto.request.MensajeRequest;
import com.oruga.gaming.dto.response.ApiResponse;
import com.oruga.gaming.entity.Mensaje;
import com.oruga.gaming.entity.Usuario;
import com.oruga.gaming.repository.UsuarioRepository;
import com.oruga.gaming.service.FileStorageService;
import com.oruga.gaming.service.MensajeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller para sistema de chat (grupal y privado)
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final MensajeService mensajeService;
    private final FileStorageService fileStorageService;
    private final UsuarioRepository usuarioRepository;

    /**
     * GET /api/chat/publico
     * Obtiene mensajes del chat grupal público
     */
    @GetMapping("/publico")
    public ResponseEntity<ApiResponse<List<Mensaje>>> obtenerMensajesGrupales() {
        List<Mensaje> mensajes = mensajeService.obtenerMensajesGrupales();
        return ResponseEntity.ok(
                ApiResponse.success("Mensajes obtenidos exitosamente", mensajes)
        );
    }

    /**
     * POST /api/chat/publico
     * Envía un mensaje de texto al chat grupal
     */
    @PostMapping("/publico")
    public ResponseEntity<ApiResponse<Mensaje>> enviarMensajeGrupal(
            @RequestParam("contenido") String contenido,
            HttpServletRequest request) {

        Long usuarioId = (Long) request.getAttribute("usuarioId");

        // Obtener nombre del usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Enviar mensaje
        Mensaje mensaje = mensajeService.enviarMensajeGrupal(
                usuarioId,
                usuario.getNombreUsuario(),
                contenido,
                null
        );

        return ResponseEntity.ok(
                ApiResponse.success("Mensaje enviado exitosamente", mensaje)
        );
    }

    /**
     * POST /api/chat/publico-imagen
     * Envía una imagen (con texto opcional) al chat grupal
     * Content-Type: multipart/form-data
     */
    @PostMapping(value = "/publico-imagen", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Mensaje>> enviarImagenGrupal(
            @RequestParam(value = "contenido", required = false, defaultValue = "") String contenido,
            @RequestParam("imagen") MultipartFile imagen,
            HttpServletRequest request) {

        Long usuarioId = (Long) request.getAttribute("usuarioId");

        // Obtener nombre del usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Guardar imagen
        String imagenUrl = fileStorageService.guardarImagenMensaje(imagen);

        // Enviar mensaje con imagen
        Mensaje mensaje = mensajeService.enviarMensajeGrupal(
                usuarioId,
                usuario.getNombreUsuario(),
                contenido,
                imagenUrl
        );

        return ResponseEntity.ok(
                ApiResponse.success("Imagen enviada exitosamente", mensaje)
        );
    }

    /**
     * POST /api/chat/privado
     * Envía un mensaje privado a otro usuario
     */
    @PostMapping("/privado")
    public ResponseEntity<ApiResponse<Mensaje>> enviarMensajePrivado(
            @RequestBody MensajeRequest request,
            HttpServletRequest httpRequest) {

        Long usuarioId = (Long) httpRequest.getAttribute("usuarioId");

        // Obtener nombre del usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Enviar mensaje privado
        Mensaje mensaje = mensajeService.enviarMensajePrivado(
                usuarioId,
                usuario.getNombreUsuario(),
                request,
                null
        );

        return ResponseEntity.ok(
                ApiResponse.success("Mensaje privado enviado exitosamente", mensaje)
        );
    }

    /**
     * POST /api/chat/privado-imagen
     * Envía una imagen privada a otro usuario
     * Content-Type: multipart/form-data
     */
    @PostMapping(value = "/privado-imagen", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Mensaje>> enviarImagenPrivada(
            @RequestParam("destinatarioId") Long destinatarioId,
            @RequestParam(value = "contenido", required = false, defaultValue = "") String contenido,
            @RequestParam("imagen") MultipartFile imagen,
            HttpServletRequest request) {

        Long usuarioId = (Long) request.getAttribute("usuarioId");

        // Obtener nombre del usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Guardar imagen
        String imagenUrl = fileStorageService.guardarImagenMensaje(imagen);

        // Crear request
        MensajeRequest mensajeRequest = new MensajeRequest();
        mensajeRequest.setDestinatarioId(destinatarioId);
        mensajeRequest.setContenido(contenido);

        // Enviar mensaje privado con imagen
        Mensaje mensaje = mensajeService.enviarMensajePrivado(
                usuarioId,
                usuario.getNombreUsuario(),
                mensajeRequest,
                imagenUrl
        );

        return ResponseEntity.ok(
                ApiResponse.success("Imagen privada enviada exitosamente", mensaje)
        );
    }

    /**
     * GET /api/chat/conversaciones/{id}/mensajes
     * Obtiene mensajes de una conversación privada
     */
    @GetMapping("/conversaciones/{id}/mensajes")
    public ResponseEntity<ApiResponse<List<Mensaje>>> obtenerMensajesConversacion(
            @PathVariable Long id) {

        List<Mensaje> mensajes = mensajeService.obtenerMensajesPorConversacion(id);
        return ResponseEntity.ok(
                ApiResponse.success("Mensajes de conversación obtenidos exitosamente", mensajes)
        );
    }

    /**
     * GET /api/chat/mensajes-no-leidos
     * Obtiene la cantidad de mensajes no leídos del usuario
     */
    @GetMapping("/mensajes-no-leidos")
    public ResponseEntity<ApiResponse<Long>> contarMensajesNoLeidos(HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");
        Long cantidad = mensajeService.contarMensajesNoLeidos(usuarioId);
        return ResponseEntity.ok(
                ApiResponse.success("Cantidad de mensajes no leídos", cantidad)
        );
    }

    /**
     * POST /api/chat/marcar-leidos
     * Marca todos los mensajes del usuario como leídos
     */
    @PostMapping("/marcar-leidos")
    public ResponseEntity<ApiResponse<Void>> marcarComoLeidos(HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");
        mensajeService.marcarComoLeidos(usuarioId);
        return ResponseEntity.ok(
                ApiResponse.success("Mensajes marcados como leídos", null)
        );
    }
}