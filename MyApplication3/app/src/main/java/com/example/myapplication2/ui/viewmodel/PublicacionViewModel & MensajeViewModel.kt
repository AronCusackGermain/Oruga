package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dtos.MensajeDto
import com.example.myapplication.data.remote.dtos.PublicacionDto
import com.example.myapplication.domain.repository.OrugaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel para publicaciones - SOLO BACKEND
 */
class PublicacionViewModel : ViewModel() {

    private val repository = OrugaRepository()

    private val _publicaciones = MutableStateFlow<List<PublicacionDto>>(emptyList())
    val publicaciones: StateFlow<List<PublicacionDto>> = _publicaciones.asStateFlow()

    private val _anuncios = MutableStateFlow<List<PublicacionDto>>(emptyList())
    val anuncios: StateFlow<List<PublicacionDto>> = _anuncios.asStateFlow()

    private val _publicacionActual = MutableStateFlow<PublicacionDto?>(null)
    val publicacionActual: StateFlow<PublicacionDto?> = _publicacionActual.asStateFlow()

    init {
        cargarPublicaciones()
        cargarAnuncios()
    }

    /**
     * Cargar todas las publicaciones
     */
    fun cargarPublicaciones() {
        viewModelScope.launch {
            repository.getPublicaciones()
                .onSuccess { lista ->
                    _publicaciones.value = lista
                }
                .onFailure { error ->
                    // Manejar error
                }
        }
    }

    /**
     * Cargar anuncios
     */
    fun cargarAnuncios() {
        viewModelScope.launch {
            repository.getAnuncios()
                .onSuccess { lista ->
                    _anuncios.value = lista
                }
                .onFailure { error ->
                    // Manejar error
                }
        }
    }

    /**
     * Crear publicación
     */
    fun crearPublicacion(
        token: String,
        titulo: String,
        contenido: String,
        esAnuncio: Boolean = false,
        imagen: File? = null,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            repository.crearPublicacion(token, titulo, contenido, esAnuncio, imagen)
                .onSuccess { publicacion ->
                    // Recargar publicaciones
                    cargarPublicaciones()
                    if (esAnuncio) cargarAnuncios()

                    onResult(true, "✅ Publicación creada")
                }
                .onFailure { error ->
                    onResult(false, error.message ?: "Error al crear publicación")
                }
        }
    }

    /**
     * Dar like
     */
    fun darLike(token: String, publicacionId: Long) {
        viewModelScope.launch {
            repository.darLike(token, publicacionId)
                .onSuccess {
                    // Recargar publicaciones para actualizar contador
                    cargarPublicaciones()
                }
                .onFailure { error ->
                    // Manejar error
                }
        }
    }

    /**
     * Cargar publicación por ID
     */
    fun cargarPublicacionPorId(publicacionId: Long) {
        // Buscar en la lista cargada
        _publicacionActual.value = _publicaciones.value.find { it.id == publicacionId }
    }

    /**
     * Obtener imágenes de una publicación (si las hubiera)
     */
    fun obtenerImagenes(publicacion: PublicacionDto): List<String> {
        // Por ahora solo retorna la imagen principal si existe
        return if (publicacion.imagenUrl != null) {
            listOf(publicacion.imagenUrl)
        } else {
            emptyList()
        }
    }
}
/**
* ViewModel para mensajes de chat - SOLO BACKEND
*/
class MensajeViewModel : ViewModel() {

    private val repository = OrugaRepository()

    private val _mensajesGrupales = MutableStateFlow<List<MensajeDto>>(emptyList())
    val mensajesGrupales: StateFlow<List<MensajeDto>> = _mensajesGrupales.asStateFlow()

    private val _mensajesPrivados = MutableStateFlow<List<MensajeDto>>(emptyList())
    val mensajesPrivados: StateFlow<List<MensajeDto>> = _mensajesPrivados.asStateFlow()

    private val _mensajesNoLeidos = MutableStateFlow(0L)
    val mensajesNoLeidos: StateFlow<Long> = _mensajesNoLeidos.asStateFlow()

    /**
     * Cargar mensajes del chat grupal
     */
    fun cargarMensajesGrupales() {
        viewModelScope.launch {
            repository.getMensajesGrupales()
                .onSuccess { mensajes ->
                    _mensajesGrupales.value = mensajes
                }
                .onFailure { error ->
                    // Manejar error
                }
        }
    }

    /**
     * Enviar mensaje grupal
     */
    fun enviarMensajeGrupal(
        token: String,
        contenido: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            repository.enviarMensajeGrupal(token, contenido)
                .onSuccess { mensaje ->
                    // Recargar mensajes
                    cargarMensajesGrupales()
                    onResult(true, "Mensaje enviado")
                }
                .onFailure { error ->
                    onResult(false, error.message ?: "Error al enviar mensaje")
                }
        }
    }

    /**
     * Cargar mensajes no leídos
     */
    fun contarMensajesNoLeidos(token: String) {
        viewModelScope.launch {
            // TODO: Implementar cuando el backend tenga este endpoint
            // Por ahora dejamos en 0
            _mensajesNoLeidos.value = 0
        }
    }

    /**
     * Marcar mensajes como leídos
     */
    fun marcarComoLeidos(token: String) {
        viewModelScope.launch {
            // TODO: Implementar cuando el backend tenga este endpoint
        }
    }
}