package com.oruga.backend.controller;

import com.oruga.backend.dto.request.EnviarMensajeGrupalRequest;
import com.oruga.backend.dto.request.EnviarMensajePrivadoRequest;
import com.oruga.backend.dto.response.MensajeResponse;
import com.oruga.backend.service.MensajeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MensajeController {

    private final MensajeService mensajeService;

    @PostMapping("/grupal")
    public ResponseEntity<MensajeResponse> enviarGrupal(
            @Valid @RequestBody EnviarMensajeGrupalRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(mensajeService.enviarMensajeGrupal(request, auth));
    }

    @GetMapping("/grupal")
    public ResponseEntity<List<MensajeResponse>> obtenerGrupales() {
        return ResponseEntity.ok(mensajeService.obtenerMensajesGrupales());
    }

    @PostMapping("/privado")
    public ResponseEntity<MensajeResponse> enviarPrivado(
            @Valid @RequestBody EnviarMensajePrivadoRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(mensajeService.enviarMensajePrivado(request, auth));
    }

    @GetMapping("/privado/{usuarioId}")
    public ResponseEntity<List<MensajeResponse>> obtenerPrivados(
            @PathVariable Long usuarioId,
            Authentication auth
    ) {
        return ResponseEntity.ok(mensajeService.obtenerMensajesPrivados(usuarioId, auth));
    }
}