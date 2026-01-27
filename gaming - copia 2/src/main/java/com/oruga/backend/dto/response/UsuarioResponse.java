package com.oruga.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime; /**
 * DTO para respuesta de Usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String email;
    private String nombreUsuario;
    private Boolean esModerador;
    private Boolean estaBaneado;
    private LocalDateTime fechaBaneo;
    private String razonBaneo;
    private String urlFotoPerfil;
    private String descripcion;
    private String steamId;
    private String discordId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimaConexion;
    private Boolean estadoConexion;
    private Integer cantidadPublicaciones;
    private Integer cantidadMensajes;
    private Integer cantidadReportes;
}
