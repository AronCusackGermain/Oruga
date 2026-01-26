package com.oruga.gaming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad ConversacionParticipante - Relación entre usuarios y conversaciones
 * Gestiona mensajes no leídos por participante
 */
@Entity
@Table(name = "conversaciones_participantes")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversacionParticipante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversacion_id", nullable = false)
    private Long conversacionId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "ultimo_mensaje_leido_id")
    private Long ultimoMensajeLeidoId = 0L;

    @Column(name = "mensajes_no_leidos")
    private Integer mensajesNoLeidos = 0;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Incrementa el contador de mensajes no leídos
     */
    public void incrementarMensajesNoLeidos() {
        this.mensajesNoLeidos++;
    }

    /**
     * Marca todos los mensajes como leídos
     */
    public void marcarComoLeido(Long ultimoMensajeId) {
        this.mensajesNoLeidos = 0;
        this.ultimoMensajeLeidoId = ultimoMensajeId;
    }
}
