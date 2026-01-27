package com.oruga.gaming.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "publicaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 200)
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "El contenido es obligatorio")
    @Column(nullable = false, length = 5000)
    private String contenido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @Column(nullable = false)
    private Integer likes = 0;

    @Column(nullable = false)
    private Integer cantidadComentarios = 0;

    @Column(nullable = false)
    private Boolean esAnuncio = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Método de utilidad para tiempo relativo
    public String getTiempoRelativo() {
        LocalDateTime ahora = LocalDateTime.now();
        long minutos = java.time.Duration.between(fechaCreacion, ahora).toMinutes();

        if (minutos < 1) return "Hace un momento";
        if (minutos < 60) return "Hace " + minutos + " minuto" + (minutos > 1 ? "s" : "");

        long horas = minutos / 60;
        if (horas < 24) return "Hace " + horas + " hora" + (horas > 1 ? "s" : "");

        long dias = horas / 24;
        if (dias < 7) return "Hace " + dias + " día" + (dias > 1 ? "s" : "");

        long semanas = dias / 7;
        if (semanas < 4) return "Hace " + semanas + " semana" + (semanas > 1 ? "s" : "");

        long meses = dias / 30;
        return "Hace " + meses + " mes" + (meses > 1 ? "es" : "");
    }
}

@Entity
@Table(name = "comentarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El contenido es obligatorio")
    @Size(min = 1, max = 1000)
    @Column(nullable = false, length = 1000)
    private String contenido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publicacion_id", nullable = false)
    private Publicacion publicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}