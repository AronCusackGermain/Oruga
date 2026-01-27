package com.example.myapplication.data.remote

import com.example.myapplication.data.remote.dto.AuthResponse
import com.example.myapplication.data.remote.dto.CrearPublicacionRequest
import com.example.myapplication.data.remote.dto.LoginRequest
import com.example.myapplication.data.remote.dto.PublicacionResponse
import com.example.myapplication.data.remote.dto.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // ========== AUTH ==========
    @POST("api/auth/register")
    suspend fun registrar(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    // ========== PUBLICACIONES ==========
    @GET("api/publicaciones")
    suspend fun obtenerPublicaciones(
        @Header("Authorization") token: String
    ): Response<List<PublicacionResponse>>

    @POST("api/publicaciones")
    suspend fun crearPublicacion(
        @Header("Authorization") token: String,
        @Body request: CrearPublicacionRequest
    ): Response<PublicacionResponse>

    @GET("api/publicaciones/{id}")
    suspend fun obtenerPublicacion(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<PublicacionResponse>

    @POST("api/publicaciones/{id}/like")
    suspend fun darLike(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Void>

    @DELETE("api/publicaciones/{id}")
    suspend fun eliminarPublicacion(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Void>

    // ========== MENSAJES ==========
    @POST("api/mensajes/grupal")
    suspend fun enviarMensajeGrupal(
        @Header("Authorization") token: String,
        @Body request: EnviarMensajeGrupalRequest
    ): Response<MensajeResponse>

    @GET("api/mensajes/grupal")
    suspend fun obtenerMensajesGrupales(
        @Header("Authorization") token: String
    ): Response<List<MensajeResponse>>

    @POST("api/mensajes/privado")
    suspend fun enviarMensajePrivado(
        @Header("Authorization") token: String,
        @Body request: EnviarMensajePrivadoRequest
    ): Response<MensajeResponse>

    @GET("api/mensajes/privado/{usuarioId}")
    suspend fun obtenerMensajesPrivados(
        @Header("Authorization") token: String,
        @Path("usuarioId") usuarioId: Long
    ): Response<List<MensajeResponse>>

    // ========== USUARIOS ==========
    @GET("api/usuarios")
    suspend fun obtenerUsuarios(
        @Header("Authorization") token: String
    ): Response<List<UsuarioResponse>>

    @GET("api/usuarios/perfil")
    suspend fun obtenerPerfil(
        @Header("Authorization") token: String
    ): Response<UsuarioResponse>

    @PUT("api/usuarios/perfil")
    suspend fun actualizarPerfil(
        @Header("Authorization") token: String,
        @Body request: ActualizarPerfilRequest
    ): Response<UsuarioResponse>
}