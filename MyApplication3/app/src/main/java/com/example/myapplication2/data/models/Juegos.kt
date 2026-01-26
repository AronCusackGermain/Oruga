package com.example.myapplication.data.models


/**
 * Modelo de datos para Juego
 * Representa videojuegos en el catálogo
 */
data class Juego(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val genero: String,
    val precio: Double,
    val imagenUrl: Int, // Resource ID para imágenes locales
    val plataformas: String,
    val desarrollador: String,
    val fechaLanzamiento: String,
    val calificacion: Float = 0f
)