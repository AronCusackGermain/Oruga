package com.example.myapplication.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.data.dao.MensajeDao
import com.example.myapplication.data.dao.PublicacionDao
import com.example.myapplication.data.dao.UsuarioDao
import com.example.myapplication.data.models.Mensaje
import com.example.myapplication.data.models.Publicacion
import com.example.myapplication.data.models.Usuario
import com.example.myapplication.data.dao.ReporteDao
import com.example.myapplication.data.models.Reporte


/**
 * Base de datos principal de la aplicaciÃ³n Oruga
 * Implementa el patrÃ³n Singleton para garantizar una Ãºnica instancia
 */
@Database(
    entities = [Usuario::class, Publicacion::class, Mensaje::class, Reporte::class],
    version = 2, // Incrementamos versiÃ³n
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun publicacionDao(): PublicacionDao
    abstract fun mensajeDao(): MensajeDao
    abstract fun reporteDao(): ReporteDao

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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Base de datos principal de la aplicaciÃ³n Oruga
 * Implementa el patrÃ³n Singleton para garantizar una Ãºnica instancia
 */
/*@Database(
    entities = [Usuario::class, Publicacion::class, Mensaje::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun publicacionDao(): PublicacionDao
    abstract fun mensajeDao(): MensajeDao

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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}*/
