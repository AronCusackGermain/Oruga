package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.models.Publicacion
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones con Publicacion en la base de datos
 */
@Dao
interface PublicacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPublicacion(publicacion: Publicacion): Long

    @Query("SELECT * FROM publicaciones ORDER BY fechaPublicacion DESC")
    fun obtenerTodasPublicaciones(): Flow<List<Publicacion>>

    @Query("SELECT * FROM publicaciones WHERE esAnuncio = 1 ORDER BY fechaPublicacion DESC")
    fun obtenerAnuncios(): Flow<List<Publicacion>>

    @Query("SELECT * FROM publicaciones WHERE autorId = :autorId ORDER BY fechaPublicacion DESC")
    fun obtenerPublicacionesPorAutor(autorId: Int): Flow<List<Publicacion>>

    @Query("SELECT * FROM publicaciones WHERE id = :id")
    suspend fun obtenerPublicacionPorId(id: Int): Publicacion?

    @Update
    suspend fun actualizarPublicacion(publicacion: Publicacion)

    @Query("UPDATE publicaciones SET cantidadLikes = cantidadLikes + 1 WHERE id = :publicacionId")
    suspend fun incrementarLikes(publicacionId: Int)

    @Query("UPDATE publicaciones SET cantidadComentarios = cantidadComentarios + 1 WHERE id = :publicacionId")
    suspend fun incrementarComentarios(publicacionId: Int)

    @Delete
    suspend fun eliminarPublicacion(publicacion: Publicacion)
}
