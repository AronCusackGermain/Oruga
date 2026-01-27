package com.oruga.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; /**
 * DTO para actualizar conexiones externas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConexionesExternasRequest {
    private String steamId;
    private String discordId;
}
