package com.oruga.gaming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad Usuario - Representa un usuario del sistema
 * Incluye campos para perfil, roles (Usuario/Moderador), conexiones externas y estadísticas
 */
@Entity
@Table(name = "usuarios")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "nombre_usuario", nullable = false, length = 50)
    private String nombreUsuario;

    // Roles y permisos
    @Column(name = "es_moderador")
    private Boolean esModerador = false;

    @Column(name = "esta_baneado")
    private Boolean estaBaneado = false;

    @Column(name = "fecha_baneo")
    private LocalDateTime fechaBaneo;

    @Column(name = "razon_baneo", columnDefinition = "TEXT")
    private String razonBaneo;

    // Perfil
    @Column(name = "url_foto_perfil")
    private String urlFotoPerfil;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "steam_id", length = 50)
    private String steamId;

    @Column(name = "discord_id", length = 50)
    private String discordId;

    // Metadata
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "ultima_conexion")
    private LocalDateTime ultimaConexion = LocalDateTime.now();

    @Column(name = "estado_conexion")
    private Boolean estadoConexion = false;

    // Estadísticas
    @Column(name = "cantidad_publicaciones")
    private Integer cantidadPublicaciones = 0;

    @Column(name = "cantidad_mensajes")
    private Integer cantidadMensajes = 0;

    @Column(name = "cantidad_reportes")
    private Integer cantidadReportes = 0;

    // Auditoría
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Método auxiliar para verificar si el usuario puede moderar
     */
    public boolean puedeModerar() {
        return esModerador != null && esModerador && !estaBaneado;
    }

    /**
     * Método auxiliar para obtener el rol como string
     */
    public String getRol() {
        return esModerador ? "MODERADOR" : "USUARIO";
    }
}
