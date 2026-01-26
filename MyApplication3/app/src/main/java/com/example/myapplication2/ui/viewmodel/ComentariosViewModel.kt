package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dtos.ComentarioDto
import com.example.myapplication.domain.repository.OrugaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para comentarios - SOLO BACKEND
 */
class ComentarioViewModel : ViewModel() {

    private val repository = OrugaRepository()

    private val _comentarios = MutableStateFlow<List<ComentarioDto>>(emptyList())
    val comentarios: StateFlow<List<ComentarioDto>> = _comentarios.asStateFlow()

    /**
     * Cargar comentarios de una publicación
     */
    fun cargarComentarios(publicacionId: Long) {
        viewModelScope.launch {
            repository.getComentarios(publicacionId)
                .onSuccess { lista ->
                    _comentarios.value = lista
                }
                .onFailure { error ->
                    // Manejar error
                }
        }
    }

    /**
     * Crear comentario
     */
    fun crearComentario(
        token: String,
        publicacionId: Long,
        contenido: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            repository.crearComentario(token, publicacionId, contenido)
                .onSuccess { comentario ->
                    // Recargar comentarios
                    cargarComentarios(publicacionId)
                    onResult(true, "✅ Comentario publicado")
                }
                .onFailure { error ->
                    onResult(false, error.message ?: "Error al comentar")
                }
        }
    }
}