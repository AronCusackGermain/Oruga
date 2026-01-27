package com.oruga.gaming.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50)
    @Column(nullable = false)
    private String nombreUsuario;

    @Column(nullable = false)
    private Boolean esModerador = false;

    @Column(nullable = false)
    private Boolean estaBaneado = false;

    private LocalDateTime fechaBaneo;

    @Column(length = 500)
    private String razonBaneo;

    @Column(length = 1000)
    private String descripcion;

    private String urlFotoPerfil;

    private String steamId;

    private String discordId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    private LocalDateTime ultimaConexion;

    @Column(nullable = false)
    private Boolean estadoConexion = false;

    @Column(nullable = false)
    private Integer cantidadPublicaciones = 0;

    @Column(nullable = false)
    private Integer cantidadMensajes = 0;

    @Column(nullable = false)
    private Integer cantidadReportes = 0;

    // Métodos de utilidad
    public boolean puedeModerar() {
        return esModerador && !estaBaneado;
    }
}