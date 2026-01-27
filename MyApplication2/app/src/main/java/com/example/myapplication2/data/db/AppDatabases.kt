package com.example.myapplication.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.data.dao.*
import com.example.myapplication.data.models.*

/**
 * Base de datos principal de la aplicación Oruga - ACTUALIZADA
 * Versión 3: Agrega soporte para Comentarios y Carrito
 */
@Database(
    entities = [
        Usuario::class,
        Publicacion::class,
        Mensaje::class,
        Reporte::class,
        Comentario::class,
        ItemCarrito::class
    ],
    version = 3, // Incrementamos versión
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun publicacionDao(): PublicacionDao
    abstract fun mensajeDao(): MensajeDao
    abstract fun reporteDao(): ReporteDao
    abstract fun comentarioDao(): ComentarioDao
    abstract fun carritoDao(): CarritoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "oruga_database"
                )
                    .fallbackToDestructiveMigration() // IMPORTANTE: Borra datos al cambiar esquema
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
