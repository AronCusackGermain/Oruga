package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.Mensaje
import com.example.myapplication.data.models.Publicacion
import com.example.myapplication.domain.repository.MensajeRepository
import com.example.myapplication.domain.repository.PublicacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar Publicaciones
 */
class PublicacionViewModel(private val repository: PublicacionRepository) : ViewModel() {

    private val _publicaciones = MutableStateFlow<List<Publicacion>>(emptyList())
    val publicaciones: StateFlow<List<Publicacion>> = _publicaciones.asStateFlow()

    private val _anuncios = MutableStateFlow<List<Publicacion>>(emptyList())
    val anuncios: StateFlow<List<Publicacion>> = _anuncios.asStateFlow()

    private val _publicacionActual = MutableStateFlow<Publicacion?>(null)
    val publicacionActual: StateFlow<Publicacion?> = _publicacionActual.asStateFlow()

    init {
        cargarPublicaciones()
        cargarAnuncios()
    }

    private fun cargarPublicaciones() {
        viewModelScope.launch {
            repository.obtenerTodasPublicaciones().collect { lista ->
                _publicaciones.value = lista
            }
        }
    }

    private fun cargarAnuncios() {
        viewModelScope.launch {
            repository.obtenerAnuncios().collect { lista ->
                _anuncios.value = lista
            }
        }
    }

    /**
     * Crear publicaciÃ³n CON imÃ¡genes
     */
    fun crearPublicacion(
        autorId: Int,
        autorNombre: String,
        titulo: String,
        contenido: String,
        imagePaths: List<String> = emptyList(), // NUEVO: Lista de rutas de imÃ¡genes
        esAnuncio: Boolean = false,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.crearPublicacion(
                autorId, autorNombre, titulo, contenido, imagePaths, esAnuncio
            )
            result.fold(
                onSuccess = {
                    val mensaje = if (imagePaths.isNotEmpty()) {
                        "Publicacion creada con ${imagePaths.size} imagen(es)"
                    } else {
                        "Publicacion creada"
                    }
                    onResult(true, mensaje)
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error")
                }
            )
        }
    }

    /**
     * Cargar una publicaciÃ³n especÃ­fica por ID
     */
    fun cargarPublicacionPorId(publicacionId: Int) {
        viewModelScope.launch {
            val publicacion = repository.obtenerPublicacionPorId(publicacionId)
            _publicacionActual.value = publicacion
        }
    }

    /**
     * Obtener imÃ¡genes de una publicaciÃ³n
     */
    fun obtenerImagenes(publicacion: Publicacion): List<String> {
        return repository.obtenerImagenesDePublicacion(publicacion)
    }

    fun darLike(publicacionId: Int) {
        viewModelScope.launch {
            repository.darLike(publicacionId)
        }
    }
}

/**
 * ViewModel para gestionar Mensajes
 * ACTUALIZADO: Soporte para imÃ¡genes
 */
class MensajeViewModel(private val repository: MensajeRepository) : ViewModel() {

    private val _mensajesGrupales = MutableStateFlow<List<Mensaje>>(emptyList())
    val mensajesGrupales: StateFlow<List<Mensaje>> = _mensajesGrupales.asStateFlow()

    private val _mensajesPrivados = MutableStateFlow<List<Mensaje>>(emptyList())
    val mensajesPrivados: StateFlow<List<Mensaje>> = _mensajesPrivados.asStateFlow()

    private val _mensajesNoLeidos = MutableStateFlow(0)
    val mensajesNoLeidos: StateFlow<Int> = _mensajesNoLeidos.asStateFlow()

    fun cargarMensajesGrupales() {
        viewModelScope.launch {
            repository.obtenerMensajesGrupales().collect { mensajes ->
                _mensajesGrupales.value = mensajes
            }
        }
    }

    fun cargarMensajesPrivados(usuarioId: Int, otroUsuarioId: Int) {
        viewModelScope.launch {
            repository.obtenerMensajesPrivados(usuarioId, otroUsuarioId).collect { mensajes ->
                _mensajesPrivados.value = mensajes
            }
        }
    }

    /**
     * Enviar mensaje grupal CON imagen opcional
     */
    fun enviarMensajeGrupal(
        remitenteId: Int,
        remitenteNombre: String,
        contenido: String,
        imagenPath: String = "", // NUEVO: Ruta de imagen
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.enviarMensajeGrupal(
                remitenteId, remitenteNombre, contenido, imagenPath
            )
            result.fold(
                onSuccess = { onResult(true, "Mensaje enviado") },
                onFailure = { error -> onResult(false, error.message ?: "Error") }
            )
        }
    }

    /**
     * Enviar mensaje privado CON imagen opcional
     */
    fun enviarMensajePrivado(
        remitenteId: Int,
        remitenteNombre: String,
        destinatarioId: Int,
        contenido: String,
        imagenPath: String = "", // NUEVO: Ruta de imagen
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.enviarMensajePrivado(
                remitenteId, remitenteNombre, destinatarioId, contenido, imagenPath
            )
            result.fold(
                onSuccess = { onResult(true, "Mensaje enviado") },
                onFailure = { error -> onResult(false, error.message ?: "Error") }
            )
        }
    }

    fun contarMensajesNoLeidos(usuarioId: Int) {
        viewModelScope.launch {
            repository.contarMensajesNoLeidos(usuarioId).collect { count ->
                _mensajesNoLeidos.value = count
            }
        }
    }
}
