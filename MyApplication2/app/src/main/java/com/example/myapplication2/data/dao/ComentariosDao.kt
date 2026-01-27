package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.models.Comentario
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones con Comentario en la base de datos
 */
@Dao
interface ComentarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarComentario(comentario: Comentario): Long

    @Query("SELECT * FROM comentarios WHERE publicacionId = :publicacionId ORDER BY fechaComentario ASC")
    fun obtenerComentariosPorPublicacion(publicacionId: Int): Flow<List<Comentario>>

    @Query("SELECT COUNT(*) FROM comentarios WHERE publicacionId = :publicacionId")
    suspend fun contarComentarios(publicacionId: Int): Int

    @Delete
    suspend fun eliminarComentario(comentario: Comentario)

    @Query("DELETE FROM comentarios WHERE publicacionId = :publicacionId")
    suspend fun eliminarComentariosPorPublicacion(publicacionId: Int)
}
