package com.oruga.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; /**
 * DTO para actualizar perfil
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarPerfilRequest {
    private String descripcion;
    private String urlFotoPerfil;
}
