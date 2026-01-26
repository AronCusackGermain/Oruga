package com.oruga.gaming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad ConfiguracionBancaria - Configuración de cuenta bancaria para recibir transferencias
 */
@Entity
@Table(name = "configuracion_bancaria")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_titular", nullable = false, length = 100)
    private String nombreTitular;

    @Column(nullable = false, length = 100)
    private String banco;

    @Column(name = "tipo_cuenta", nullable = false, length = 50)
    private String tipoCuenta; // Cuenta Corriente, Cuenta Vista, etc.

    @Column(name = "numero_cuenta", nullable = false, length = 50)
    private String numeroCuenta;

    @Column(name = "rut_titular", nullable = false, length = 20)
    private String rutTitular;

    @Column(name = "email_confirmacion", length = 100)
    private String emailConfirmacion;

    private Boolean activo = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
