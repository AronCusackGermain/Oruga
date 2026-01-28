package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.models.ItemCarrito
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones con Carrito en la base de datos
 */
@Dao
interface CarritoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarAlCarrito(item: ItemCarrito): Long

    @Query("SELECT * FROM carrito WHERE usuarioId = :usuarioId ORDER BY fechaAgregado DESC")
    fun obtenerCarritoPorUsuario(usuarioId: Int): Flow<List<ItemCarrito>>

    @Query("SELECT * FROM carrito WHERE usuarioId = :usuarioId AND juegoId = :juegoId LIMIT 1")
    suspend fun buscarItemEnCarrito(usuarioId: Int, juegoId: Int): ItemCarrito?

    @Query("UPDATE carrito SET cantidad = :cantidad WHERE id = :itemId")
    suspend fun actualizarCantidad(itemId: Int, cantidad: Int)

    @Query("SELECT COUNT(*) FROM carrito WHERE usuarioId = :usuarioId")
    fun contarItemsEnCarrito(usuarioId: Int): Flow<Int>

    @Delete
    suspend fun eliminarDelCarrito(item: ItemCarrito)

    @Query("DELETE FROM carrito WHERE usuarioId = :usuarioId")
    suspend fun vaciarCarrito(usuarioId: Int)
}