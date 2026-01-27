package com.oruga.backend.model;

import com.oruga.backend.model.enums.TipoMensaje;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad Mensaje - Replica del modelo Android
 * Representa mensajes en chats (grupal o privado)
 */
@Entity
@Table(name = "mensajes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remitente_id", nullable = false)
    private Usuario remitente;

    @Column(nullable = false)
    private String remitenteNombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_id")
    private Usuario destinatario;

    @Column(nullable = false, length = 2000)
    private String contenido;

    // URL de imagen adjunta
    @Column(length = 500)
    private String imagenUrl = "";

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaEnvio;

    @Column(nullable = false)
    private Boolean esGrupal = true;

    @Column(nullable = false)
    private Boolean leido = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMensaje tipoMensaje = TipoMensaje.TEXTO;

    /**
     * Verificar si tiene imagen
     */
    public boolean tieneImagen() {
        return imagenUrl != null && !imagenUrl.isEmpty();
    }
}