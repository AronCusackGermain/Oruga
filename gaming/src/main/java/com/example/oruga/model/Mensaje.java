package com.example.oruga.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Mensaje - Compatible con Room Database
 */
@Entity
@Table(name = "mensajes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer remitenteId;

    @NotBlank
    @Column(nullable = false)
    private String remitenteNombre;

    private Integer destinatarioId;

    @NotBlank
    @Column(nullable = false, length = 2000)
    private String contenido;

    private String imagenUrl = "";

    @Column(nullable = false)
    private Long fechaEnvio = System.currentTimeMillis();

    @Column(nullable = false)
    private Boolean esGrupal = true;

    @Column(nullable = false)
    private Boolean leido = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMensaje tipoMensaje = TipoMensaje.TEXTO;

    public enum TipoMensaje {
        TEXTO,
        IMAGEN,
        TEXTO_CON_IMAGEN
    }
}