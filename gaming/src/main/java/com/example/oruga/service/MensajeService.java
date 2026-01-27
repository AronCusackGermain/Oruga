package com.example.oruga.service;

import com.example.oruga.model.Mensaje;
import com.example.oruga.repository.MensajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de Mensaje
 */
@Service
public class MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    /**
     * Enviar mensaje grupal
     */
    @Transactional
    public Mensaje enviarMensajeGrupal(Mensaje mensaje) {
        if (mensaje.getContenido() == null || mensaje.getContenido().isBlank()) {
            throw new RuntimeException("El mensaje no puede estar vacío");
        }

        mensaje.setEsGrupal(true);
        mensaje.setFechaEnvio(System.currentTimeMillis());
        return mensajeRepository.save(mensaje);
    }

    /**
     * Enviar mensaje privado
     */
    @Transactional
    public Mensaje enviarMensajePrivado(Mensaje mensaje) {
        if (mensaje.getContenido() == null || mensaje.getContenido().isBlank()) {
            throw new RuntimeException("El mensaje no puede estar vacío");
        }
        if (mensaje.getDestinatarioId() == null) {
            throw new RuntimeException("Debe especificar un destinatario");
        }

        mensaje.setEsGrupal(false);
        mensaje.setFechaEnvio(System.currentTimeMillis());
        return mensajeRepository.save(mensaje);
    }

    /**
     * Obtener mensajes grupales
     */
    public List<Mensaje> obtenerMensajesGrupales() {
        return mensajeRepository.findByEsGrupalOrderByFechaEnvioAsc(true);
    }

    /**
     * Obtener mensajes privados entre dos usuarios
     */
    public List<Mensaje> obtenerMensajesPrivados(Integer usuarioId, Integer otroUsuarioId) {
        return mensajeRepository.findMensajesPrivados(usuarioId, otroUsuarioId);
    }

    /**
     * Contar mensajes no leídos
     */
    public Integer contarNoLeidos(Integer usuarioId) {
        return mensajeRepository.countByDestinatarioIdAndLeidoFalse(usuarioId);
    }
}