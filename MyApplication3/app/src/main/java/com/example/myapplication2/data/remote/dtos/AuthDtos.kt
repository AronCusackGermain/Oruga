package com.example.myapplication.data.remote.dtos

// ========== REQUEST ==========
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val nombreUsuario: String,
    val codigoModerador: String = ""
)

// ========== RESPONSE ==========
data class JwtResponse(
    val token: String,
    val type: String,
    val usuario: UsuarioDto
)

data class UsuarioDto(
    val id: Long,
    val email: String,
    val nombreUsuario: String,
    val esModerador: Boolean,
    val estaBaneado: Boolean?,
    val urlFotoPerfil: String?,
    val descripcion: String?,
    val steamId: String?,
    val discordId: String?,
    val cantidadPublicaciones: Int,
    val cantidadMensajes: Int
)