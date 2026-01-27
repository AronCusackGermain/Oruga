package com.example.myapplication.domain.repository

import android.util.Log
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.data.remote.dtos.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * Repositorio principal para comunicación con el backend de Oruga
 * Reemplaza completamente Room Database
 */
class OrugaRepository {

    private val api = RetrofitClient.apiService

    // ==================== AUTENTICACIÓN ====================

    suspend fun login(email: String, password: String): Result<JwtResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Error en login"))
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Credenciales inválidas"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Log.e("OrugaRepository", "Error login", e)
                Result.failure(e)
            }
        }

    suspend fun register(
        email: String,
        password: String,
        nombreUsuario: String,
        codigoModerador: String = ""
    ): Result<JwtResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.register(
                RegisterRequest(email, password, nombreUsuario, codigoModerador)
            )
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Error en registro"))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al registrar"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("OrugaRepository", "Error register", e)
            Result.failure(e)
        }
    }

    suspend fun logout(token: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.logout("Bearer $token")
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al cerrar sesión"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== JUEGOS ====================

    suspend fun getJuegos(): Result<List<JuegoDto>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getJuegos()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener juegos"))
            }
        } catch (e: Exception) {
            Log.e("OrugaRepository", "Error getJuegos", e)
            Result.failure(e)
        }
    }

    suspend fun getJuegoPorId(id: Long): Result<JuegoDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.getJuegoPorId(id)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Juego no encontrado"))
            } else {
                Result.failure(Exception("Error al obtener juego"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== CARRITO ====================

    suspend fun getCarrito(token: String): Result<CarritoResponseDto> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getCarrito("Bearer $token")
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Carrito vacío"))
                } else {
                    Result.failure(Exception("Error al obtener carrito"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun agregarAlCarrito(
        token: String,
        juegoId: Long,
        cantidad: Int
    ): Result<CarritoResponseDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.agregarAlCarrito(
                "Bearer $token",
                AgregarCarritoRequest(juegoId, cantidad)
            )
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Error al agregar"))
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun vaciarCarrito(token: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.vaciarCarrito("Bearer $token")
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al vaciar carrito"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== PUBLICACIONES ====================

    suspend fun getPublicaciones(): Result<List<PublicacionDto>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getPublicaciones()
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()?.data ?: emptyList())
                } else {
                    Result.failure(Exception("Error al obtener publicaciones"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getAnuncios(): Result<List<PublicacionDto>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAnuncios()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener anuncios"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearPublicacion(
        token: String,
        titulo: String,
        contenido: String,
        esAnuncio: Boolean,
        imagen: File?
    ): Result<PublicacionDto> = withContext(Dispatchers.IO) {
        try {
            val tituloBody = titulo.toRequestBody("text/plain".toMediaTypeOrNull())
            val contenidoBody = contenido.toRequestBody("text/plain".toMediaTypeOrNull())
            val esAnuncioBody = esAnuncio.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val imagenPart = imagen?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("imagen", it.name, requestFile)
            }

            val response = api.crearPublicacion(
                "Bearer $token",
                tituloBody,
                contenidoBody,
                esAnuncioBody,
                imagenPart
            )

            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Error al crear publicación"))
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun darLike(token: String, publicacionId: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.darLike("Bearer $token", publicacionId)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error al dar like"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // ==================== COMENTARIOS ====================

    suspend fun getComentarios(publicacionId: Long): Result<List<ComentarioDto>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getComentarios(publicacionId)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()?.data ?: emptyList())
                } else {
                    Result.failure(Exception("Error al obtener comentarios"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun crearComentario(
        token: String,
        publicacionId: Long,
        contenido: String
    ): Result<ComentarioDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.crearComentario(
                "Bearer $token",
                publicacionId,
                ComentarioRequest(contenido)
            )
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Error al crear comentario"))
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== CHAT ====================

    suspend fun getMensajesGrupales(): Result<List<MensajeDto>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getMensajesGrupales()
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()?.data ?: emptyList())
                } else {
                    Result.failure(Exception("Error al obtener mensajes"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun enviarMensajeGrupal(
        token: String,
        contenido: String
    ): Result<MensajeDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.enviarMensajeGrupal("Bearer $token", contenido)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Error al enviar mensaje"))
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== CHECKOUT ====================

    suspend fun iniciarCheckout(token: String): Result<CheckoutResponseDto> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.iniciarCheckout("Bearer $token")
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Error al iniciar checkout"))
                } else {
                    Result.failure(Exception("Error: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getOrdenes(token: String): Result<List<OrdenDto>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getOrdenes("Bearer $token")
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()?.data ?: emptyList())
                } else {
                    Result.failure(Exception("Error al obtener órdenes"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    suspend fun buscarJuegosPorGenero(genero: String): Result<List<JuegoDto>> = withContext(Dispatchers.IO) {
        try {
            // Se asume que tu apiService tiene un método getJuegos o similar que acepta el género
            val response = api.getJuegos() // O api.getJuegosPorGenero(genero) si existe en tu ApiService

            if (response.isSuccessful && response.body()?.success == true) {
                val todosLosJuegos = response.body()?.data ?: emptyList()

                // Filtramos la lista por el género recibido
                val juegosFiltrados = todosLosJuegos.filter { juego ->
                    juego.genero.contains(genero, ignoreCase = true)
                }

                Result.success(juegosFiltrados)
            } else {
                Result.failure(Exception("Error al obtener juegos por género"))
            }
        } catch (e: Exception) {
            Log.e("OrugaRepository", "Error buscarJuegosPorGenero", e)
            Result.failure(e)
        }
    }
}
