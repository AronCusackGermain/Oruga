package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.EstadoReporte
import com.example.myapplication.data.models.Reporte
import com.example.myapplication.data.models.TipoReporte
import com.example.myapplication.data.models.Usuario
import com.example.myapplication.domain.repository.EstadisticasComunidad
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

    private val _reportes = MutableStateFlow<List<Reporte>>(emptyList())
    val reportes: StateFlow<List<Reporte>> = _reportes.asStateFlow()

    private val _reportesPendientes = MutableStateFlow(0)
    val reportesPendientes: StateFlow<Int> = _reportesPendientes.asStateFlow()

    private val _usuariosBaneados = MutableStateFlow<List<Usuario>>(emptyList())
    val usuariosBaneados: StateFlow<List<Usuario>> = _usuariosBaneados.asStateFlow()

    private val _estadisticas = MutableStateFlow<EstadisticasComunidad?>(null)
    val estadisticas: StateFlow<EstadisticasComunidad?> = _estadisticas.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            // Cargar reportes
            repository.obtenerTodosReportes().collect { lista ->
                _reportes.value = lista
            }
        }

        viewModelScope.launch {
            // Contar reportes pendientes
            repository.contarReportesPendientes().collect { count ->
                _reportesPendientes.value = count
            }
        }

        viewModelScope.launch {
            // Cargar usuarios baneados
            repository.obtenerUsuariosBaneados().collect { lista ->
                _usuariosBaneados.value = lista
            }
        }

        viewModelScope.launch {
            // Cargar estadísticas
            val stats = repository.obtenerEstadisticas()
            _estadisticas.value = stats
        }
    }

    /**
     * Banear un usuario
     */
    fun banearUsuario(
        moderadorId: Int,
        usuarioId: Int,
        razon: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.banearUsuario(moderadorId, usuarioId, true, razon)
            result.fold(
                onSuccess = {
                    onResult(true, "Usuario baneado correctamente")
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
        moderadorId: Int,
        usuarioId: Int,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.banearUsuario(moderadorId, usuarioId, false, "")
            result.fold(
                onSuccess = {
                    onResult(true, "Usuario desbaneado correctamente")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al desbanear usuario")
                }
            )
        }
    }

    /**
     * Eliminar una publicación
     */
    fun eliminarPublicacion(
        moderadorId: Int,
        publicacionId: Int,
        razon: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.eliminarPublicacion(moderadorId, publicacionId, razon)
            result.fold(
                onSuccess = {
                    onResult(true, "Publicación eliminada")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al eliminar")
                }
            )
        }
    }

    /**
     * Crear un reporte
     */
    fun crearReporte(
        reportadoPorId: Int,
        reportadoPorNombre: String,
        tipoReporte: TipoReporte,
        idContenido: Int,
        razon: String,
        descripcion: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.crearReporte(
                reportadoPorId,
                reportadoPorNombre,
                tipoReporte,
                idContenido,
                razon,
                descripcion
            )
            result.fold(
                onSuccess = {
                    onResult(true, "Reporte enviado. Será revisado por un moderador.")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al enviar reporte")
                }
            )
        }
    }

    /**
     * Resolver un reporte
     */
    fun resolverReporte(
        moderadorId: Int,
        moderadorNombre: String,
        reporteId: Int,
        accionTomada: String,
        aceptado: Boolean,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.resolverReporte(
                moderadorId,
                moderadorNombre,
                reporteId,
                accionTomada,
                aceptado
            )
            result.fold(
                onSuccess = {
                    onResult(true, "Reporte resuelto")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al resolver")
                }
            )
        }
    }

    /**
     * Filtrar reportes por estado
     */
    fun filtrarReportesPorEstado(estado: EstadoReporte) {
        viewModelScope.launch {
            repository.obtenerReportesPorEstado(estado).collect { lista ->
                _reportes.value = lista
            }
        }
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