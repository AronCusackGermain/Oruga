package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.models.EstadoReporte
import com.example.myapplication.data.models.Reporte
import com.example.myapplication.data.models.TipoReporte
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones con Reporte en la base de datos
 */
@Dao
interface ReporteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarReporte(reporte: Reporte): Long

    @Query("SELECT * FROM reportes ORDER BY fechaReporte DESC")
    fun obtenerTodosReportes(): Flow<List<Reporte>>

    @Query("SELECT * FROM reportes WHERE estado = :estado ORDER BY fechaReporte DESC")
    fun obtenerReportesPorEstado(estado: EstadoReporte): Flow<List<Reporte>>

    @Query("SELECT * FROM reportes WHERE tipoReporte = :tipo ORDER BY fechaReporte DESC")
    fun obtenerReportesPorTipo(tipo: TipoReporte): Flow<List<Reporte>>

    @Query("SELECT * FROM reportes WHERE id = :id")
    suspend fun obtenerReportePorId(id: Int): Reporte?

    @Query("SELECT * FROM reportes WHERE idContenido = :idContenido AND tipoReporte = :tipo")
    suspend fun obtenerReportesPorContenido(idContenido: Int, tipo: TipoReporte): List<Reporte>

    @Update
    suspend fun actualizarReporte(reporte: Reporte)

    @Query("""
        UPDATE reportes 
        SET estado = :estado, 
            moderadorId = :moderadorId, 
            moderadorNombre = :moderadorNombre,
            fechaResolucion = :fechaResolucion,
            accionTomada = :accionTomada
        WHERE id = :reporteId
    """)
    suspend fun resolverReporte(
        reporteId: Int,
        estado: EstadoReporte,
        moderadorId: Int,
        moderadorNombre: String,
        fechaResolucion: Long,
        accionTomada: String
    )

    @Query("SELECT COUNT(*) FROM reportes WHERE estado = :estado")
    fun contarReportesPorEstado(estado: EstadoReporte): Flow<Int>

    @Delete
    suspend fun eliminarReporte(reporte: Reporte)
}
