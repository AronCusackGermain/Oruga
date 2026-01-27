package com.oruga.gaming.service;

import com.oruga.gaming.dto.ConversacionDto;
import com.oruga.gaming.dto.MensajeDto;
import com.oruga.gaming.entity.Mensaje;
import com.oruga.gaming.entity.TipoMensaje;
import com.oruga.gaming.entity.Usuario;
import com.oruga.gaming.repository.MensajeRepository;
import com.oruga.gaming.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;

    public MensajeService(MensajeRepository mensajeRepository, 
                         UsuarioRepository usuarioRepository) {
        this.mensajeRepository = mensajeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Enviar mensaje grupal
     */
    public MensajeDto enviarMensajeGrupal(String email, String contenido, String imagenUrl) {
        Usuario remitente = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (contenido.isBlank() && (imagenUrl == null || imagenUrl.isBlank())) {
            throw new RuntimeException("Debes enviar un mensaje o una imagen");
        }

        TipoMensaje tipo = determinarTipoMensaje(contenido, imagenUrl);

        Mensaje mensaje = Mensaje.builder()
                .remitenteId(remitente.getId())
                .remitenteNombre(remitente.getNombreUsuario())
                .contenido(contenido != null ? contenido : "")
                .imagenUrl(imagenUrl)
                .esGrupal(true)
                .tipoMensaje(tipo)
                .leido(false)
                .build();

        Mensaje mensajeGuardado = mensajeRepository.save(mensaje);
        return toDto(mensajeGuardado);
    }

    /**
     * Enviar mensaje privado
     */
    public MensajeDto enviarMensajePrivado(String email, Long destinatarioId, 
                                           String contenido, String imagenUrl) {
        Usuario remitente = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Usuario destinatario = usuarioRepository.findById(destinatarioId)
                .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));

        if (contenido.isBlank() && (imagenUrl == null || imagenUrl.isBlank())) {
            throw new RuntimeException("Debes enviar un mensaje o una imagen");
        }

        TipoMensaje tipo = determinarTipoMensaje(contenido, imagenUrl);

        Mensaje mensaje = Mensaje.builder()
                .remitenteId(remitente.getId())
                .remitenteNombre(remitente.getNombreUsuario())
                .destinatarioId(destinatarioId)
                .contenido(contenido != null ? contenido : "")
                .imagenUrl(imagenUrl)
                .esGrupal(false)
                .tipoMensaje(tipo)
                .leido(false)
                .build();

        Mensaje mensajeGuardado = mensajeRepository.save(mensaje);
        return toDto(mensajeGuardado);
    }

    /**
     * Obtener mensajes grupales
     */
    public List<MensajeDto> obtenerMensajesGrupales() {
        return mensajeRepository.findByEsGrupalTrueOrderByFechaEnvioAsc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtener mensajes privados entre dos usuarios
     */
    public List<MensajeDto> obtenerMensajesPrivados(String email, Long otroUsuarioId) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return mensajeRepository.findMensajesPrivados(usuario.getId(), otroUsuarioId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtener lista de conversaciones
     */
    public List<ConversacionDto> obtenerConversaciones(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Long> usuariosIds = mensajeRepository.findConversaciones(usuario.getId());
        List<ConversacionDto> conversaciones = new ArrayList<>();

        for (Long otroUsuarioId : usuariosIds) {
            Usuario otroUsuario = usuarioRepository.findById(otroUsuarioId).orElse(null);
            if (otroUsuario == null) continue;

            List<Mensaje> mensajes = mensajeRepository.findMensajesPrivados(
                    usuario.getId(), otroUsuarioId);

            if (!mensajes.isEmpty()) {
                Mensaje ultimo = mensajes.get(mensajes.size() - 1);
                
                Long noLeidos = mensajes.stream()
                        .filter(m -> m.getDestinatarioId().equals(usuario.getId()) 
                                && !m.getLeido())
                        .count();

                ConversacionDto conv = ConversacionDto.builder()
                        .usuarioId(otroUsuario.getId())
                        .nombreUsuario(otroUsuario.getNombreUsuario())
                        .ultimoMensaje(ultimo.getContenido())
                        .fechaUltimoMensaje(ultimo.getFechaEnvio())
                        .mensajesNoLeidos(noLeidos.intValue())
                        .estaConectado(otroUsuario.getEstadoConexion())
                        .build();

                conversaciones.add(conv);
            }
        }

        return conversaciones;
    }

    /**
     * Marcar mensajes como leídos
     */
    public void marcarComoLeidos(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        mensajeRepository.marcarComoLeidos(usuario.getId());
    }

    /**
     * Contar mensajes no leídos
     */
    public Long contarNoLeidos(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return mensajeRepository.countByDestinatarioIdAndLeidoFalse(usuario.getId());
    }

    /**
     * Eliminar mensaje (solo si eres el remitente o moderador)
     */
    public void eliminarMensaje(String email, Long mensajeId) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Mensaje mensaje = mensajeRepository.findById(mensajeId)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));

        if (!mensaje.getRemitenteId().equals(usuario.getId()) && !usuario.getEsModerador()) {
            throw new RuntimeException("No tienes permiso para eliminar este mensaje");
        }

        mensajeRepository.delete(mensaje);
    }

    // Métodos auxiliares

    private TipoMensaje determinarTipoMensaje(String contenido, String imagenUrl) {
        boolean tieneTexto = contenido != null && !contenido.isBlank();
        boolean tieneImagen = imagenUrl != null && !imagenUrl.isBlank();

        if (tieneTexto && tieneImagen) {
            return TipoMensaje.TEXTO_CON_IMAGEN;
        } else if (tieneImagen) {
            return TipoMensaje.IMAGEN;
        } else {
            return TipoMensaje.TEXTO;
        }
    }

    private MensajeDto toDto(Mensaje mensaje) {
        return MensajeDto.builder()
                .id(mensaje.getId())
                .remitenteId(mensaje.getRemitenteId())
                .remitenteNombre(mensaje.getRemitenteNombre())
                .destinatarioId(mensaje.getDestinatarioId())
                .contenido(mensaje.getContenido())
                .imagenUrl(mensaje.getImagenUrl())
                .fechaEnvio(mensaje.getFechaEnvio())
                .esGrupal(mensaje.getEsGrupal())
                .leido(mensaje.getLeido())
                .tipoMensaje(mensaje.getTipoMensaje())
                .tiempoRelativo(calcularTiempoRelativo(mensaje.getFechaEnvio()))
                .build();
    }

    private String calcularTiempoRelativo(LocalDateTime fechaEnvio) {
        Duration duration = Duration.between(fechaEnvio, LocalDateTime.now());
        long minutos = duration.toMinutes();

        if (minutos < 1) return "Ahora";
        if (minutos < 60) return "Hace " + minutos + " min";

        long horas = duration.toHours();
        if (horas < 24) return "Hace " + horas + " h";

        long dias = duration.toDays();
        if (dias < 7) return "Hace " + dias + " d";

        long semanas = dias / 7;
        return "Hace " + semanas + " sem";
    }
}
