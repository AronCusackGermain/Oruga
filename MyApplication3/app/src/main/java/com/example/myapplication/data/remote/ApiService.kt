package com.example.myapplication.data.remote

import com.example.myapplication.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

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

    @POST("api/auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<Void>

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

    // ========== COMENTARIOS ==========
    @POST("api/comentarios")
    suspend fun crearComentario(
        @Header("Authorization") token: String,
        @Body request: CrearComentarioRequest
    ): Response<ComentarioResponse>

    @GET("api/comentarios/publicacion/{publicacionId}")
    suspend fun obtenerComentarios(
        @Header("Authorization") token: String,
        @Path("publicacionId") publicacionId: Long
    ): Response<List<ComentarioResponse>>

    @DELETE("api/comentarios/{id}")
    suspend fun eliminarComentario(
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

    @PUT("api/usuarios/cuentas-externas")
    suspend fun conectarCuentasExternas(
        @Header("Authorization") token: String,
        @Query("steamId") steamId: String?,
        @Query("discordId") discordId: String?
    ): Response<UsuarioResponse>

    // ========== MODERACIÓN ==========
    @POST("api/moderacion/banear/{usuarioId}")
    suspend fun banearUsuario(
        @Header("Authorization") token: String,
        @Path("usuarioId") usuarioId: Long,
        @Body request: BanearUsuarioRequest
    ): Response<Void>

    @POST("api/moderacion/desbanear/{usuarioId}")
    suspend fun desbanearUsuario(
        @Header("Authorization") token: String,
        @Path("usuarioId") usuarioId: Long
    ): Response<Void>

    @GET("api/moderacion/usuarios-baneados")
    suspend fun obtenerUsuariosBaneados(
        @Header("Authorization") token: String
    ): Response<List<UsuarioResponse>>

    @DELETE("api/moderacion/publicacion/{publicacionId}")
    suspend fun eliminarPublicacionPorModerador(
        @Header("Authorization") token: String,
        @Path("publicacionId") publicacionId: Long,
        @Body request: EliminarContenidoRequest
    ): Response<Void>

    @DELETE("api/moderacion/comentario/{comentarioId}")
    suspend fun eliminarComentarioPorModerador(
        @Header("Authorization") token: String,
        @Path("comentarioId") comentarioId: Long,
        @Body request: EliminarContenidoRequest
    ): Response<Void>

    // ========== REPORTES ==========
    @POST("api/reportes")
    suspend fun crearReporte(
        @Header("Authorization") token: String,
        @Body request: CrearReporteRequest
    ): Response<ReporteResponse>

    @GET("api/reportes")
    suspend fun obtenerReportes(
        @Header("Authorization") token: String
    ): Response<List<ReporteResponse>>

    @GET("api/reportes/pendientes")
    suspend fun obtenerReportesPendientes(
        @Header("Authorization") token: String
    ): Response<List<ReporteResponse>>

    @PUT("api/reportes/{reporteId}/resolver")
    suspend fun resolverReporte(
        @Header("Authorization") token: String,
        @Path("reporteId") reporteId: Long,
        @Body request: ResolverReporteRequest
    ): Response<ReporteResponse>

    @GET("api/reportes/estadisticas")
    suspend fun obtenerEstadisticas(
        @Header("Authorization") token: String
    ): Response<EstadisticasResponse>

    // ========== CARRITO ==========
    @POST("api/carrito/agregar")
    suspend fun agregarAlCarrito(
        @Header("Authorization") token: String,
        @Body request: AgregarCarritoRequest
    ): Response<ItemCarritoResponse>

    @GET("api/carrito")
    suspend fun obtenerCarrito(
        @Header("Authorization") token: String
    ): Response<List<ItemCarritoResponse>>

    @PUT("api/carrito/item/{itemId}")
    suspend fun actualizarCantidad(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: Long,
        @Body request: ActualizarCantidadRequest
    ): Response<ItemCarritoResponse>

    @DELETE("api/carrito/item/{itemId}")
    suspend fun eliminarItem(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: Long
    ): Response<Void>

    @DELETE("api/carrito/vaciar")
    suspend fun vaciarCarrito(
        @Header("Authorization") token: String
    ): Response<Void>

    @GET("api/carrito/contar")
    suspend fun contarItems(
        @Header("Authorization") token: String
    ): Response<Long>

    @GET("api/carrito/total")
    suspend fun calcularTotal(
        @Header("Authorization") token: String
    ): Response<Double>

    @POST("api/carrito/procesar")
    suspend fun procesarCompra(
        @Header("Authorization") token: String
    ): Response<CompraResponse>
}