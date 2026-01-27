package com.oruga.gaming.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del remitente es obligatorio")
    @Column(nullable = false)
    private Long remitenteId;

    @NotBlank(message = "El nombre del remitente es obligatorio")
    @Column(nullable = false)
    private String remitenteNombre;

    // Null si es mensaje grupal
    @Column(nullable = true)
    private Long destinatarioId;

    @NotBlank(message = "El contenido es obligatorio")
    @Column(nullable = false, length = 2000)
    private String contenido;

    @Column(length = 500)
    private String imagenUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaEnvio;

    @Column(nullable = false)
    private Boolean esGrupal = true;

    @Column(nullable = false)
    private Boolean leido = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMensaje tipoMensaje = TipoMensaje.TEXTO;

    // Método de utilidad
    public boolean tieneImagen() {
        return imagenUrl != null && !imagenUrl.isBlank();
    }
}