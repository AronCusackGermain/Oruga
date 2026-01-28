package com.example.myapplication.data.remote.dto

data class UsuarioResponse(
    val id: Long,
    val email: String,
    val nombreUsuario: String,
    val esModerador: Boolean,
    val estaBaneado: Boolean,
    val fechaBaneo: String?,
    val razonBaneo: String,
    val urlFotoPerfil: String,
    val descripcion: String,
    val steamId: String,
    val discordId: String,
    val fechaCreacion: String,
    val ultimaConexion: String,
    val estadoConexion: Boolean,
    val cantidadPublicaciones: Int,
    val cantidadMensajes: Int,
    val cantidadReportes: Int
)

data class ActualizarPerfilRequest(
    val descripcion: String?,
    val urlFotoPerfil: String?
)