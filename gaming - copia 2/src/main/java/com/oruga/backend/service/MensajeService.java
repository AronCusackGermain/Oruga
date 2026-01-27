package com.oruga.backend.service;

import com.oruga.backend.dto.request.EnviarMensajeGrupalRequest;
import com.oruga.backend.dto.request.EnviarMensajePrivadoRequest;
import com.oruga.backend.dto.response.MensajeResponse;
import com.oruga.backend.model.Mensaje;
import com.oruga.backend.model.Usuario;
import com.oruga.backend.model.enums.TipoMensaje;
import com.oruga.backend.repository.MensajeRepository;
import com.oruga.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public MensajeResponse enviarMensajeGrupal(EnviarMensajeGrupalRequest request, Authentication auth) {
        Usuario remitente = obtenerUsuarioAutenticado(auth);

        TipoMensaje tipo = determinarTipoMensaje(request.getContenido(), request.getImagenUrl());

        Mensaje mensaje = Mensaje.builder()
                .remitente(remitente)
                .remitenteNombre(remitente.getNombreUsuario())
                .contenido(request.getContenido())
                .imagenUrl(request.getImagenUrl() != null ? request.getImagenUrl() : "")
                .esGrupal(true)
                .tipoMensaje(tipo)
                .build();

        mensajeRepository.save(mensaje);
        remitente.setCantidadMensajes(remitente.getCantidadMensajes() + 1);
        usuarioRepository.save(remitente);

        return mapearAResponse(mensaje);
    }

    @Transactional
    public MensajeResponse enviarMensajePrivado(EnviarMensajePrivadoRequest request, Authentication auth) {
        Usuario remitente = obtenerUsuarioAutenticado(auth);
        Usuario destinatario = usuarioRepository.findById(request.getDestinatarioId())
                .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));

        TipoMensaje tipo = determinarTipoMensaje(request.getContenido(), request.getImagenUrl());

        Mensaje mensaje = Mensaje.builder()
                .remitente(remitente)
                .remitenteNombre(remitente.getNombreUsuario())
                .destinatario(destinatario)
                .contenido(request.getContenido())
                .imagenUrl(request.getImagenUrl() != null ? request.getImagenUrl() : "")
                .esGrupal(false)
                .tipoMensaje(tipo)
                .build();

        mensajeRepository.save(mensaje);
        return mapearAResponse(mensaje);
    }

    public List<MensajeResponse> obtenerMensajesGrupales() {
        return mensajeRepository.findByEsGrupalOrderByFechaEnvioAsc(true)
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    public List<MensajeResponse> obtenerMensajesPrivados(Long otroUsuarioId, Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        return mensajeRepository.findMensajesPrivados(usuario.getId(), otroUsuarioId)
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    private TipoMensaje determinarTipoMensaje(String contenido, String imagenUrl) {
        boolean tieneTexto = contenido != null && !contenido.isBlank();
        boolean tieneImagen = imagenUrl != null && !imagenUrl.isBlank();

        if (tieneTexto && tieneImagen) return TipoMensaje.TEXTO_CON_IMAGEN;
        if (tieneImagen) return TipoMensaje.IMAGEN;
        return TipoMensaje.TEXTO;
    }

    private Usuario obtenerUsuarioAutenticado(Authentication auth) {
        return usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private MensajeResponse mapearAResponse(Mensaje mensaje) {
        return MensajeResponse.builder()
                .id(mensaje.getId())
                .remitenteNombre(mensaje.getRemitenteNombre())
                .contenido(mensaje.getContenido())
                .imagenUrl(mensaje.getImagenUrl())
                .fechaEnvio(mensaje.getFechaEnvio())
                .esGrupal(mensaje.getEsGrupal())
                .tipoMensaje(mensaje.getTipoMensaje().name())
                .build();
    }
}