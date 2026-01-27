package com.oruga.backend.controller;

import com.oruga.backend.dto.request.CrearPublicacionRequest;
import com.oruga.backend.dto.response.PublicacionResponse;
import com.oruga.backend.service.PublicacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publicaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PublicacionController {

    private final PublicacionService publicacionService;

    @PostMapping
    public ResponseEntity<PublicacionResponse> crear(
            @Valid @RequestBody CrearPublicacionRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(publicacionService.crearPublicacion(request, auth));
    }

    @GetMapping
    public ResponseEntity<List<PublicacionResponse>> obtenerTodas() {
        return ResponseEntity.ok(publicacionService.obtenerTodasPublicaciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicacionResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(publicacionService.obtenerPorId(id));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> darLike(@PathVariable Long id) {
        publicacionService.darLike(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        publicacionService.eliminarPublicacion(id, auth);
        return ResponseEntity.ok().build();
    }
}