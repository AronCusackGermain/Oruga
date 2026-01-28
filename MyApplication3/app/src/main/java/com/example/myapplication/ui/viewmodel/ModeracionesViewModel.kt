package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.EstadisticasResponse
import com.example.myapplication.data.remote.dto.ReporteResponse
import com.example.myapplication.data.remote.dto.UsuarioResponse
import com.example.myapplication.domain.repository.ModeracionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para funciones de moderación
 */
class ModeracionViewModel(
    private val repository: ModeracionRepository
) : ViewModel() {

    private val _reportes = MutableStateFlow<List<ReporteResponse>>(emptyList())
    val reportes: StateFlow<List<ReporteResponse>> = _reportes.asStateFlow()

    private val _reportesPendientesCount = MutableStateFlow(0)
    val reportesPendientesCount: StateFlow<Int> = _reportesPendientesCount.asStateFlow()

    private val _usuariosBaneados = MutableStateFlow<List<UsuarioResponse>>(emptyList())
    val usuariosBaneados: StateFlow<List<UsuarioResponse>> = _usuariosBaneados.asStateFlow()

    private val _estadisticas = MutableStateFlow<EstadisticasResponse?>(null)
    val estadisticas: StateFlow<EstadisticasResponse?> = _estadisticas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Cargar reportes
                repository.obtenerReportes().onSuccess { lista ->
                    _reportes.value = lista
                }.onFailure { e ->
                    _error.value = e.message
                }

                // Cargar reportes pendientes para el contador
                repository.obtenerReportesPendientes().onSuccess { lista ->
                    _reportesPendientesCount.value = lista.size
                }

                // Cargar usuarios baneados
                repository.obtenerUsuariosBaneados().onSuccess { lista ->
                    _usuariosBaneados.value = lista
                }

                // Cargar estadísticas
                repository.obtenerEstadisticas().onSuccess { stats ->
                    _estadisticas.value = stats
                }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Banear un usuario
     */
    fun banearUsuario(
        usuarioId: Long,
        razon: String,
        duracionDias: Int? = null,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.banearUsuario(usuarioId, razon, duracionDias)
            result.fold(
                onSuccess = {
                    onResult(true, "Usuario baneado correctamente")
                    cargarDatos() // Recargar para actualizar lista de baneados
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al banear usuario")
                }
            )
        }
    }

    /**
     * Desbanear un usuario
     */
    fun desbanearUsuario(
        usuarioId: Long,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.desbanearUsuario(usuarioId)
            result.fold(
                onSuccess = {
                    onResult(true, "Usuario desbaneado correctamente")
                    cargarDatos()
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al desbanear usuario")
                }
            )
        }
    }

    /**
     * Eliminar una publicación como moderador
     */
    fun eliminarPublicacion(
        publicacionId: Long,
        razon: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.eliminarPublicacion(publicacionId, razon)
            result.fold(
                onSuccess = {
                    onResult(true, "Publicación eliminada")
                    cargarDatos()
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al eliminar")
                }
            )
        }
    }

    /**
     * Resolver un reporte
     */
    fun resolverReporte(
        reporteId: Long,
        aceptado: Boolean,
        accionTomada: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.resolverReporte(reporteId, aceptado, accionTomada)
            result.fold(
                onSuccess = {
                    onResult(true, "Reporte resuelto")
                    cargarDatos()
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al resolver")
                }
            )
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}

/**
 * Factory para ModeracionViewModel
 */
class ModeracionViewModelFactory(
    private val repository: ModeracionRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ModeracionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ModeracionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
