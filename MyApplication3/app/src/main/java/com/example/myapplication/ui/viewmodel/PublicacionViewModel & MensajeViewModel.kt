package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.MensajeResponse
import com.example.myapplication.data.remote.dto.PublicacionResponse
import com.example.myapplication.domain.repository.MensajeRepository
import com.example.myapplication.domain.repository.PublicacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PublicacionViewModel(private val repository: PublicacionRepository) : ViewModel() {

    private val _publicaciones = MutableStateFlow<List<PublicacionResponse>>(emptyList())
    val publicaciones: StateFlow<List<PublicacionResponse>> = _publicaciones.asStateFlow()

    private val _anuncios = MutableStateFlow<List<PublicacionResponse>>(emptyList())
    val anuncios: StateFlow<List<PublicacionResponse>> = _anuncios.asStateFlow()

    private val _publicacionActual = MutableStateFlow<PublicacionResponse?>(null)
    val publicacionActual: StateFlow<PublicacionResponse?> = _publicacionActual.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        cargarPublicaciones()
    }

    /**
     * Cargar todas las publicaciones
     */
    fun cargarPublicaciones() {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.obtenerTodasPublicaciones()
            result.onSuccess { lista ->
                _publicaciones.value = lista
                _anuncios.value = lista.filter { it.esAnuncio }
            }

            _isLoading.value = false
        }
    }

    /**
     * Crear publicación con imágenes
     */
    fun crearPublicacion(
        autorId: Long,
        autorNombre: String,
        titulo: String,
        contenido: String,
        imagePaths: List<String> = emptyList(),
        esAnuncio: Boolean = false,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.crearPublicacion(
                titulo, contenido, imagePaths, esAnuncio
            )

            result.fold(
                onSuccess = { publicacion ->
                    // Recargar lista
                    cargarPublicaciones()
                    val mensaje = if (imagePaths.isNotEmpty()) {
                        "Publicación creada con ${imagePaths.size} imagen(es)"
                    } else {
                        "Publicación creada"
                    }
                    onResult(true, mensaje)
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al crear publicación")
                }
            )
        }
    }

    /**
     * Cargar publicación por ID
     */
    fun cargarPublicacionPorId(publicacionId: Long) {
        viewModelScope.launch {
            val result = repository.obtenerPublicacionPorId(publicacionId)
            result.onSuccess { publicacion ->
                _publicacionActual.value = publicacion
            }
        }
    }

    /**
     * Dar like
     */
    fun darLike(publicacionId: Long) {
        viewModelScope.launch {
            repository.darLike(publicacionId)
            // Recargar publicaciones para actualizar contador
            cargarPublicaciones()
        }
    }

    /**
     * Obtener imágenes de una publicación
     */
    fun obtenerImagenes(publicacion: PublicacionResponse): List<String> {
        return repository.obtenerImagenesDePublicacion(publicacion)
    }

    /**
     * Eliminar publicación
     */
    fun eliminarPublicacion(publicacionId: Long, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.eliminarPublicacion(publicacionId)
            result.fold(
                onSuccess = {
                    cargarPublicaciones()
                    onResult(true, "Publicación eliminada")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error")
                }
            )
        }
    }
}

/**
 * Factory para PublicacionViewModel
 */
class PublicacionViewModelFactory(
    private val repository: PublicacionRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PublicacionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PublicacionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
class MensajeViewModel(private val repository: MensajeRepository) : ViewModel() {

    private val _mensajesGrupales = MutableStateFlow<List<MensajeResponse>>(emptyList())
    val mensajesGrupales: StateFlow<List<MensajeResponse>> = _mensajesGrupales.asStateFlow()

    private val _mensajesPrivados = MutableStateFlow<List<MensajeResponse>>(emptyList())
    val mensajesPrivados: StateFlow<List<MensajeResponse>> = _mensajesPrivados.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Cargar mensajes grupales
     */
    fun cargarMensajesGrupales() {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.obtenerMensajesGrupales()
            result.onSuccess { mensajes ->
                _mensajesGrupales.value = mensajes
            }

            _isLoading.value = false
        }
    }

    /**
     * Cargar mensajes privados
     */
    fun cargarMensajesPrivados(usuarioId: Long, otroUsuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.obtenerMensajesPrivados(otroUsuarioId)
            result.onSuccess { mensajes ->
                _mensajesPrivados.value = mensajes
            }

            _isLoading.value = false
        }
    }

    /**
     * Enviar mensaje grupal
     */
    fun enviarMensajeGrupal(
        remitenteId: Long,
        remitenteNombre: String,
        contenido: String,
        imagenPath: String = "",
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.enviarMensajeGrupal(contenido, imagenPath)

            result.fold(
                onSuccess = {
                    cargarMensajesGrupales() // Recargar mensajes
                    onResult(true, "Mensaje enviado")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al enviar mensaje")
                }
            )
        }
    }

    /**
     * Enviar mensaje privado
     */
    fun enviarMensajePrivado(
        remitenteId: Long,
        remitenteNombre: String,
        destinatarioId: Long,
        contenido: String,
        imagenPath: String = "",
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.enviarMensajePrivado(
                destinatarioId,
                contenido,
                imagenPath
            )

            result.fold(
                onSuccess = {
                    cargarMensajesPrivados(remitenteId, destinatarioId)
                    onResult(true, "Mensaje enviado")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al enviar mensaje")
                }
            )
        }
    }
}

/**
 * Factory para MensajeViewModel
 */
class MensajeViewModelFactory(
    private val repository: MensajeRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MensajeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MensajeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}