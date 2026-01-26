package com.oruga.gaming.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String urlFotoPerfil;
    private String descripcion;
    private String steamId;
    private String discordId;
    private Integer cantidadPublicaciones;
    private Integer cantidadMensajes;
}
