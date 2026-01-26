package com.example.myapplication.data.remote

import com.example.myapplication.data.remote.dtos.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface OrugaApiService {

    // ==================== AUTENTICACIÓN ====================
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<JwtResponse>>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<JwtResponse>>

    @POST("api/auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Void>>

    // ==================== JUEGOS ====================
    @GET("api/juegos")
    suspend fun getJuegos(): Response<ApiResponse<List<JuegoDto>>>

    @GET("api/juegos/{id}")
    suspend fun getJuegoPorId(
        @Path("id") id: Long
    ): Response<ApiResponse<JuegoDto>>

    @GET("api/juegos/buscar")
    suspend fun buscarJuegosPorGenero(
        @Query("genero") genero: String
    ): Response<ApiResponse<List<JuegoDto>>>

    // ==================== CARRITO ====================
    @GET("api/carrito")
    suspend fun getCarrito(
        @Header("Authorization") token: String
    ): Response<ApiResponse<CarritoResponseDto>>

    @POST("api/carrito/agregar")
    suspend fun agregarAlCarrito(
        @Header("Authorization") token: String,
        @Body request: AgregarCarritoRequest
    ): Response<ApiResponse<CarritoResponseDto>>

    @PUT("api/carrito/item/{juegoId}")
    suspend fun actualizarCantidad(
        @Header("Authorization") token: String,
        @Path("juegoId") juegoId: Long,
        @Query("cantidad") cantidad: Int
    ): Response<ApiResponse<Void>>

    @DELETE("api/carrito/item/{juegoId}")
    suspend fun eliminarDelCarrito(
        @Header("Authorization") token: String,
        @Path("juegoId") juegoId: Long
    ): Response<ApiResponse<Void>>

    @DELETE("api/carrito/limpiar")
    suspend fun vaciarCarrito(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Void>>

    // ==================== CHECKOUT Y ÓRDENES ====================
    @POST("api/checkout/iniciar")
    suspend fun iniciarCheckout(
        @Header("Authorization") token: String
    ): Response<ApiResponse<CheckoutResponseDto>>

    @Multipart
    @POST("api/ordenes/{id}/comprobante")
    suspend fun subirComprobante(
        @Path("id") ordenId: Long,
        @Part comprobante: MultipartBody.Part
    ): Response<ApiResponse<OrdenDto>>

    @GET("api/ordenes")
    suspend fun getOrdenes(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<OrdenDto>>>

    @GET("api/ordenes/{id}")
    suspend fun getOrdenPorId(
        @Path("id") ordenId: Long
    ): Response<ApiResponse<OrdenDto>>

    // ==================== PUBLICACIONES ====================
    @GET("api/publicaciones")
    suspend fun getPublicaciones(): Response<ApiResponse<List<PublicacionDto>>>

    @GET("api/publicaciones/{id}")
    suspend fun getPublicacionPorId(
        @Path("id") id: Long
    ): Response<ApiResponse<PublicacionDto>>

    @GET("api/publicaciones/anuncios")
    suspend fun getAnuncios(): Response<ApiResponse<List<PublicacionDto>>>

    @Multipart
    @POST("api/publicaciones")
    suspend fun crearPublicacion(
        @Header("Authorization") token: String,
        @Part("titulo") titulo: RequestBody,
        @Part("contenido") contenido: RequestBody,
        @Part("esAnuncio") esAnuncio: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Response<ApiResponse<PublicacionDto>>

    @POST("api/publicaciones/{id}/like")
    suspend fun darLike(
        @Header("Authorization") token: String,
        @Path("id") publicacionId: Long
    ): Response<ApiResponse<Void>>

    @DELETE("api/publicaciones/{id}")
    suspend fun eliminarPublicacion(
        @Header("Authorization") token: String,
        @Path("id") publicacionId: Long
    ): Response<ApiResponse<Void>>

    // ==================== COMENTARIOS ====================
    @GET("api/publicaciones/{publicacionId}/comentarios")
    suspend fun getComentarios(
        @Path("publicacionId") publicacionId: Long
    ): Response<ApiResponse<List<ComentarioDto>>>

    @POST("api/publicaciones/{publicacionId}/comentarios")
    suspend fun crearComentario(
        @Header("Authorization") token: String,
        @Path("publicacionId") publicacionId: Long,
        @Body request: ComentarioRequest
    ): Response<ApiResponse<ComentarioDto>>

    @DELETE("api/comentarios/{id}")
    suspend fun eliminarComentario(
        @Header("Authorization") token: String,
        @Path("id") comentarioId: Long
    ): Response<ApiResponse<Void>>

    // ==================== CHAT ====================
    @GET("api/chat/publico")
    suspend fun getMensajesGrupales(): Response<ApiResponse<List<MensajeDto>>>

    @FormUrlEncoded
    @POST("api/chat/publico")
    suspend fun enviarMensajeGrupal(
        @Header("Authorization") token: String,
        @Field("contenido") contenido: String
    ): Response<ApiResponse<MensajeDto>>

    @Multipart
    @POST("api/chat/publico-imagen")
    suspend fun enviarImagenGrupal(
        @Header("Authorization") token: String,
        @Part("contenido") contenido: RequestBody,
        @Part imagen: MultipartBody.Part
    ): Response<ApiResponse<MensajeDto>>

    @POST("api/chat/privado")
    suspend fun enviarMensajePrivado(
        @Header("Authorization") token: String,
        @Body request: MensajePrivadoRequest
    ): Response<ApiResponse<MensajeDto>>

    @Multipart
    @POST("api/chat/privado-imagen")
    suspend fun enviarImagenPrivada(
        @Header("Authorization") token: String,
        @Part("destinatarioId") destinatarioId: RequestBody,
        @Part("contenido") contenido: RequestBody,
        @Part imagen: MultipartBody.Part
    ): Response<ApiResponse<MensajeDto>>

    @GET("api/chat/conversaciones/{id}/mensajes")
    suspend fun getMensajesConversacion(
        @Path("id") conversacionId: Long
    ): Response<ApiResponse<List<MensajeDto>>>

    @GET("api/chat/mensajes-no-leidos")
    suspend fun getMensajesNoLeidos(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Long>>

    @POST("api/chat/marcar-leidos")
    suspend fun marcarMensajesLeidos(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Void>>

    // ==================== MODERACIÓN ====================
    @GET("api/moderacion/ordenes-pendientes")
    suspend fun getOrdenesPendientes(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<OrdenDto>>>

    @POST("api/moderacion/ordenes/{id}/revisar")
    suspend fun revisarOrden(
        @Header("Authorization") token: String,
        @Path("id") ordenId: Long,
        @Body request: RevisarOrdenRequest
    ): Response<ApiResponse<OrdenDto>>

    @POST("api/moderacion/ordenes/{id}/aprobar")
    suspend fun aprobarOrden(
        @Header("Authorization") token: String,
        @Path("id") ordenId: Long,
        @Query("comentario") comentario: String = "Orden aprobada"
    ): Response<ApiResponse<OrdenDto>>

    @POST("api/moderacion/ordenes/{id}/rechazar")
    suspend fun rechazarOrden(
        @Header("Authorization") token: String,
        @Path("id") ordenId: Long,
        @Query("comentario") comentario: String = "Orden rechazada"
    ): Response<ApiResponse<OrdenDto>>
}