package com.oruga.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad Usuario - Replica del modelo Android
 * Representa a un usuario del foro Oruga con roles diferenciados
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 20, message = "El nombre debe tener entre 3 y 20 caracteres")
    @Column(nullable = false, unique = true)
    private String nombreUsuario;

    // Roles y permisos
    @Column(nullable = false)
    private Boolean esModerador = false;

    @Column(nullable = false)
    private Boolean estaBaneado = false;

    @Column
    private LocalDateTime fechaBaneo;

    @Column(length = 500)
    private String razonBaneo = "";

    // Perfil
    @Column(length = 500)
    private String urlFotoPerfil = "";

    @Column(length = 1000)
    private String descripcion = "";

    // Conexiones externas
    @Column
    private String steamId = "";

    @Column
    private String discordId = "";

    // Metadata
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column
    private LocalDateTime ultimaConexion;

    @Column(nullable = false)
    private Boolean estadoConexion = false;

  // Estadísticas (para moderadores)
    @Column(name = "cantidad_publicaciones", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer cantidadPublicaciones = 0;

    @Column(name = "cantidad_mensajes", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer cantidadMensajes = 0;

    @Column(name = "cantidad_reportes", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer cantidadReportes = 0;

    /**
     * Método auxiliar para verificar si puede moderar
     */
    public boolean puedeModerar() {
        return esModerador && !estaBaneado;
    }

    /**
     * Método auxiliar para obtener el rol
     */
    public String obtenerRol() {
        return esModerador ? "MODERADOR" : "USUARIO";
    }

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (ultimaConexion == null) {
            ultimaConexion = LocalDateTime.now();
        }
        if (cantidadMensajes == null) {
            cantidadMensajes = 0;
        }
        if (cantidadPublicaciones == null) {
            cantidadPublicaciones = 0;
        }
        if (cantidadReportes == null) {
            cantidadReportes = 0;
        }
        if (esModerador == null) {
            esModerador = false;
        }
        if (estaBaneado == null) {
            estaBaneado = false;
        }
        if (estadoConexion == null) {
            estadoConexion = false;
        }
    }
    @PreUpdate
    protected void onUpdate() {
        ultimaConexion = LocalDateTime.now();
    }
}
