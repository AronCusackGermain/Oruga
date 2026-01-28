package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.ComentarioResponse
import com.example.myapplication.domain.repository.ComentarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar comentarios en publicaciones
 */
class ComentarioViewModel(private val repository: ComentarioRepository) : ViewModel() {

    private val _comentarios = MutableStateFlow<List<ComentarioResponse>>(emptyList())
    val comentarios: StateFlow<List<ComentarioResponse>> = _comentarios.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Cargar comentarios de una publicación
     */
    fun cargarComentarios(publicacionId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.obtenerComentarios(publicacionId)
            result.fold(
                onSuccess = { lista ->
                    _comentarios.value = lista
                },
                onFailure = { e ->
                    _error.value = e.message
                }
            )
            _isLoading.value = false
        }
    }

    /**
     * Crear un comentario
     */
    fun crearComentario(
        publicacionId: Long,
        contenido: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.crearComentario(publicacionId, contenido)
            result.fold(
                onSuccess = {
                    onResult(true, "Comentario publicado")
                    cargarComentarios(publicacionId) // Recargar lista
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al comentar")
                }
            )
            _isLoading.value = false
        }
    }

    /**
     * Eliminar un comentario
     */
    fun eliminarComentario(
        publicacionId: Long,
        comentarioId: Long,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.eliminarComentario(comentarioId)
            result.fold(
                onSuccess = { 
                    onResult(true, "Comentario eliminado")
                    cargarComentarios(publicacionId) // Recargar lista
                },
                onFailure = { error -> 
                    onResult(false, error.message ?: "Error al eliminar") 
                }
            )
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}

/**
 * Factory para ComentarioViewModel
 */
class ComentarioViewModelFactory(
    private val repository: ComentarioRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ComentarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ComentarioViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
