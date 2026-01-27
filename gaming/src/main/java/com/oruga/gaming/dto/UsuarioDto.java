package com.oruga.gaming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDto {
    private Long id;
    private String email;
    private String nombreUsuario;
    private Boolean esModerador;
    private Boolean estaBaneado;
    private String urlFotoPerfil;
    private String descripcion;
    private Integer cantidadPublicaciones;
    private Integer cantidadMensajes;
}
