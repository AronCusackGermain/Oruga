package com.example.myapplication.data.remote.dto

data class RegisterRequest(
    val email: String,
    val password: String,
    val nombreUsuario: String,
    val codigoModerador: String = ""
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val tipo: String,
    val id: Long,
    val email: String,
    val nombreUsuario: String,
    val esModerador: Boolean
)