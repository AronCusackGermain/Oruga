package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.Comentario
import com.example.myapplication.domain.repository.ComentarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar comentarios en publicaciones
 */
class ComentarioViewModel(private val repository: ComentarioRepository) : ViewModel() {

    private val _comentarios = MutableStateFlow<List<Comentario>>(emptyList())
    val comentarios: StateFlow<List<Comentario>> = _comentarios.asStateFlow()

    /**
     * Cargar comentarios de una publicaciÃ³n
     */
    fun cargarComentarios(publicacionId: Int) {
        viewModelScope.launch {
            repository.obtenerComentarios(publicacionId).collect { lista ->
                _comentarios.value = lista
            }
        }
    }

    /**
     * Crear un comentario
     */
    fun crearComentario(
        publicacionId: Int,
        autorId: Int,
        autorNombre: String,
        contenido: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.crearComentario(
                publicacionId, autorId, autorNombre, contenido
            )
            result.fold(
                onSuccess = {
                    onResult(true, "Comentario publicado")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al comentar")
                }
            )
        }
    }

    /**
     * Eliminar un comentario
     */
    fun eliminarComentario(
        comentario: Comentario,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.eliminarComentario(comentario)
            result.fold(
                onSuccess = { onResult(true, "Comentario eliminado") },
                onFailure = { error -> onResult(false, error.message ?: "Error") }
            )
        }
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
