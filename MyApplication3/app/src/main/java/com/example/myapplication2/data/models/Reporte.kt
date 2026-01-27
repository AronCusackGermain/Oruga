package com.example.myapplication.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo para reportes de contenido inapropiado
 */
@Entity(tableName = "reportes")
data class Reporte(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Usuario que reporta
    val reportadoPorId: Int,
    val reportadoPorNombre: String,

    // Tipo de reporte
    val tipoReporte: TipoReporte,
    val idContenido: Int, // ID de publicación o mensaje

    // Detalles
    val razon: String,
    val descripcion: String = "",

    // Estado
    val estado: EstadoReporte = EstadoReporte.PENDIENTE,
    val fechaReporte: Long = System.currentTimeMillis(),

    // Resolución (por moderador)
    val moderadorId: Int? = null,
    val moderadorNombre: String? = null,
    val fechaResolucion: Long? = null,
    val accionTomada: String = ""
)

/**
 * Tipos de reporte
 */
enum class TipoReporte {
    PUBLICACION,
    MENSAJE,
    USUARIO,
    OTRO
}

/**
 * Estados de reporte
 */
enum class EstadoReporte {
    PENDIENTE,
    EN_REVISION,
    RESUELTO,
    RECHAZADO
}