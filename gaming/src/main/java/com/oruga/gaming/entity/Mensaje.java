package com.oruga.gaming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad Mensaje - Mensajes de chat (públicos y privados) con soporte para imágenes
 */
@Entity
@Table(name = "mensajes")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversacion_id")
    private Long conversacionId;

    @Column(name = "remitente_id", nullable = false)
    private Long remitenteId;

    @Column(name = "remitente_nombre", nullable = false, length = 50)
    private String remitenteNombre;

    @Column(name = "destinatario_id")
    private Long destinatarioId;

    @Column(name = "tipo_mensaje", length = 20)
    private String tipoMensaje = "texto"; // texto, imagen, texto_con_imagen

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "archivo_url", length = 500)
    private String archivoUrl;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    @Column(name = "es_grupal")
    private Boolean esGrupal = true;

    private Boolean leido = false;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Verifica si el mensaje tiene imagen adjunta
     */
    public boolean tieneImagen() {
        return archivoUrl != null && !archivoUrl.isEmpty();
    }

    /**
     * Marca el mensaje como leído
     */
    public void marcarComoLeido() {
        this.leido = true;
    }

    /**
     * Determina el tipo de mensaje automáticamente
     */
    public void determinarTipo() {
        if (tieneImagen() && contenido != null && !contenido.trim().isEmpty()) {
            this.tipoMensaje = "texto_con_imagen";
        } else if (tieneImagen()) {
            this.tipoMensaje = "imagen";
        } else {
            this.tipoMensaje = "texto";
        }
    }
}
