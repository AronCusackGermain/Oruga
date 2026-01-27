package com.example.oruga.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Usuario - Compatible con Room Database del Android
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 20, message = "El nombre debe tener entre 3 y 20 caracteres")
    @Column(nullable = false)
    private String nombreUsuario;

    // Roles y permisos
    @Column(nullable = false)
    private Boolean esModerador = false;

    @Column(nullable = false)
    private Boolean estaBaneado = false;

    private Long fechaBaneo;

    private String razonBaneo = "";

    // Perfil
    private String urlFotoPerfil = "";

    private String descripcion = "";

    // Conexiones externas
    private String steamId = "";

    private String discordId = "";

    // Metadata
    @Column(nullable = false)
    private Long fechaCreacion = System.currentTimeMillis();

    @Column(nullable = false)
    private Long ultimaConexion = System.currentTimeMillis();

    @Column(nullable = false)
    private Boolean estadoConexion = false;

    // Estadísticas
    @Column(nullable = false)
    private Integer cantidadPublicaciones = 0;

    @Column(nullable = false)
    private Integer cantidadMensajes = 0;

    @Column(nullable = false)
    private Integer cantidadReportes = 0;
}