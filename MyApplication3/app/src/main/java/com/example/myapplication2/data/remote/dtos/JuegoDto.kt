package com.example.myapplication.data.remote.dtos

data class JuegoDto(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val genero: String,
    val precio: Double,
    val precioFormateado: String,
    val stock: Int,
    val imagenUrl: String?,
    val plataformas: String?,
    val desarrollador: String?,
    val fechaLanzamiento: String?,
    val calificacion: Double?,
    val activo: Boolean,

)