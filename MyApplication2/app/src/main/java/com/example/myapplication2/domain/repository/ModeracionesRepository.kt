package com.example.myapplication.domain.repository

import com.example.myapplication.data.dao.MensajeDao
import com.example.myapplication.data.dao.PublicacionDao
import com.example.myapplication.data.dao.ReporteDao
import com.example.myapplication.data.dao.UsuarioDao
import com.example.myapplication.data.models.*
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para funciones de moderaciÃ³n
 * Solo accesible por usuarios con rol de moderador
 */
class ModeracionRepository(
    private val usuarioDao: UsuarioDao,
    private val publicacionDao: PublicacionDao,
    private val mensajeDao: MensajeDao,
    private val reporteDao: ReporteDao
) {

    /**
     * Verifica si un usuario es moderador
     */
    suspend fun esModerador(usuarioId: Int): Boolean {
        val usuario = usuarioDao.buscarPorId(usuarioId)
        return usuario?.puedeModerar() ?: false
    }

    /**
     * Banear/Desbanear un usuario
     */
    suspend fun banearUsuario(
        moderadorId: Int,
        usuarioId: Int,
        banear: Boolean,
        razon: String
    ): Result<Unit> {
        return try {
            // Verificar que quien banea sea moderador
            if (!esModerador(moderadorId)) {
                return Result.failure(Exception("No tienes permisos de moderador"))
            }

            // No puede banearse a sÃ­ mismo
            if (moderadorId == usuarioId) {
                return Result.failure(Exception("No puedes banearte a ti mismo"))
            }

            val fecha = if (banear) System.currentTimeMillis() else null
            usuarioDao.banearUsuario(usuarioId, banear, fecha, razon)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener usuarios baneados
     */
    fun obtenerUsuariosBaneados(): Flow<List<Usuario>> {
        return usuarioDao.obtenerUsuariosBaneados()
    }

    /**
     * Eliminar una publicaciÃ³n
     */
    suspend fun eliminarPublicacion(
        moderadorId: Int,
        publicacionId: Int,
        razon: String
    ): Result<Unit> {
        return try {
            if (!esModerador(moderadorId)) {
                return Result.failure(Exception("No tienes permisos de moderador"))
            }

            val publicacion = publicacionDao.obtenerPublicacionPorId(publicacionId)
            if (publicacion != null) {
                publicacionDao.eliminarPublicacion(publicacion)
                Result.success(Unit)
            } else {
                Result.failure(Exception("PublicaciÃ³n no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Eliminar un mensaje
     */
    suspend fun eliminarMensaje(
        moderadorId: Int,
        mensajeId: Int,
        razon: String
    ): Result<Unit> {
        return try {
            if (!esModerador(moderadorId)) {
                return Result.failure(Exception("No tienes permisos de moderador"))
            }

            // Implementar eliminaciÃ³n de mensaje
            // (necesitarÃ­as agregar funciÃ³n en MensajeDao)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Crear un reporte
     */
    suspend fun crearReporte(
        reportadoPorId: Int,
        reportadoPorNombre: String,
        tipoReporte: TipoReporte,
        idContenido: Int,
        razon: String,
        descripcion: String
    ): Result<Long> {
        return try {
            val reporte = Reporte(
                reportadoPorId = reportadoPorId,
                reportadoPorNombre = reportadoPorNombre,
                tipoReporte = tipoReporte,
                idContenido = idContenido,
                razon = razon,
                descripcion = descripcion
            )

            val id = reporteDao.insertarReporte(reporte)

            // Incrementar contador de reportes del usuario
            usuarioDao.incrementarReportes(reportadoPorId)

            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener todos los reportes
     */
    fun obtenerTodosReportes(): Flow<List<Reporte>> {
        return reporteDao.obtenerTodosReportes()
    }

    /**
     * Obtener reportes por estado
     */
    fun obtenerReportesPorEstado(estado: EstadoReporte): Flow<List<Reporte>> {
        return reporteDao.obtenerReportesPorEstado(estado)
    }

    /**
     * Resolver un reporte
     */
    suspend fun resolverReporte(
        moderadorId: Int,
        moderadorNombre: String,
        reporteId: Int,
        accionTomada: String,
        aceptado: Boolean
    ): Result<Unit> {
        return try {
            if (!esModerador(moderadorId)) {
                return Result.failure(Exception("No tienes permisos de moderador"))
            }

            val estado = if (aceptado) EstadoReporte.RESUELTO else EstadoReporte.RECHAZADO

            reporteDao.resolverReporte(
                reporteId = reporteId,
                estado = estado,
                moderadorId = moderadorId,
                moderadorNombre = moderadorNombre,
                fechaResolucion = System.currentTimeMillis(),
                accionTomada = accionTomada
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Contar reportes pendientes
     */
    fun contarReportesPendientes(): Flow<Int> {
        return reporteDao.contarReportesPorEstado(EstadoReporte.PENDIENTE)
    }

    /**
     * Obtener estadÃ­sticas generales
     */
    suspend fun obtenerEstadisticas(): EstadisticasComunidad {
        val todosUsuarios = usuarioDao.obtenerTodosLosUsuarios()
        val todasPublicaciones = publicacionDao.obtenerTodasPublicaciones()

        // Nota: Esto es un ejemplo simplificado
        // En producciÃ³n, usarÃ­as queries especÃ­ficas para contar
        return EstadisticasComunidad(
            totalUsuarios = 0, // Se actualizarÃ­a con Flow
            usuariosActivos = 0,
            totalPublicaciones = 0,
            totalMensajes = 0,
            reportesPendientes = 0
        )
    }
}

/**
 * Clase de datos para estadÃ­sticas
 */
data class EstadisticasComunidad(
    val totalUsuarios: Int,
    val usuariosActivos: Int,
    val totalPublicaciones: Int,
    val totalMensajes: Int,
    val reportesPendientes: Int
)
