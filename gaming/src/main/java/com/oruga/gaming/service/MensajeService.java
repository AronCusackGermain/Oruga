package com.oruga.gaming.service;

import com.oruga.gaming.dto.request.MensajeRequest;
import com.oruga.gaming.entity.Conversacion;
import com.oruga.gaming.entity.Mensaje;
import com.oruga.gaming.exception.BadRequestException;
import com.oruga.gaming.repository.ConversacionRepository;
import com.oruga.gaming.repository.MensajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final ConversacionRepository conversacionRepository;
    private final UsuarioService usuarioService;

    @Transactional
    public Mensaje enviarMensajeGrupal(Long remitenteId, String remitenteNombre,
                                       String contenido, String imagenUrl) {
        if ((contenido == null || contenido.trim().isEmpty()) &&
                (imagenUrl == null || imagenUrl.trim().isEmpty())) {
            throw new BadRequestException("Debes enviar un mensaje o una imagen");
        }

        Mensaje mensaje = Mensaje.builder()
                .remitenteId(remitenteId)
                .remitenteNombre(remitenteNombre)
                .contenido(contenido != null ? contenido : "")
                .archivoUrl(imagenUrl)
                .esGrupal(true)
                .fechaEnvio(LocalDateTime.now())
                .build();

        mensaje.determinarTipo();
        mensaje = mensajeRepository.save(mensaje);

        // Incrementar contador del usuario
        usuarioService.incrementarMensajes(remitenteId);

        return mensaje;
    }

    @Transactional
    public Mensaje enviarMensajePrivado(Long remitenteId, String remitenteNombre,
                                        MensajeRequest request, String imagenUrl) {
        if ((request.getContenido() == null || request.getContenido().trim().isEmpty()) &&
                (imagenUrl == null || imagenUrl.trim().isEmpty())) {
            throw new BadRequestException("Debes enviar un mensaje o una imagen");
        }

        // Crear o encontrar conversación
        Conversacion conversacion = Conversacion.builder()
                .esGrupal(false)
                .ultimoMensaje(request.getContenido())
                .fechaUltimoMensaje(LocalDateTime.now())
                .build();
        conversacion = conversacionRepository.save(conversacion);

        Mensaje mensaje = Mensaje.builder()
                .conversacionId(conversacion.getId())
                .remitenteId(remitenteId)
                .remitenteNombre(remitenteNombre)
                .destinatarioId(request.getDestinatarioId())
                .contenido(request.getContenido() != null ? request.getContenido() : "")
                .archivoUrl(imagenUrl)
                .esGrupal(false)
                .fechaEnvio(LocalDateTime.now())
                .build();

        mensaje.determinarTipo();
        mensaje = mensajeRepository.save(mensaje);

        // Incrementar contador del usuario
        usuarioService.incrementarMensajes(remitenteId);

        return mensaje;
    }

    public List<Mensaje> obtenerMensajesGrupales() {
        return mensajeRepository.findByEsGrupalTrueOrderByFechaEnvioDesc();
    }

    public List<Mensaje> obtenerMensajesPorConversacion(Long conversacionId) {
        return mensajeRepository.findByConversacionIdOrderByFechaEnvioAsc(conversacionId);
    }

    public Long contarMensajesNoLeidos(Long usuarioId) {
        return mensajeRepository.countByDestinatarioIdAndLeidoFalse(usuarioId);
    }

    @Transactional
    public void marcarComoLeidos(Long usuarioId) {
        List<Mensaje> mensajes = mensajeRepository.findMensajesNoLeidos(usuarioId);
        mensajes.forEach(Mensaje::marcarComoLeido);
        mensajeRepository.saveAll(mensajes);
    }
}