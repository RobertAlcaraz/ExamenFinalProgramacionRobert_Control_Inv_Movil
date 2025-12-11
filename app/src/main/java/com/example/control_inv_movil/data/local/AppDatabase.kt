package com.example.control_inv_movil.data.local


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.control_inv_movil.data.local.dao.ProductoDao
import com.example.control_inv_movil.data.local.model.Producto

/**
 * La clase principal de la base de datos para la aplicación.
 * Utiliza el patrón Singleton para asegurar que solo haya una instancia de la BD.
 */
@Database(entities = [Producto::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Método abstracto que Room implementará para darnos acceso al DAO.
    abstract fun productoDao(): ProductoDao

    companion object {
        // La anotación @Volatile asegura que el valor de INSTANCE esté siempre actualizado
        // y sea visible para todos los hilos de ejecución.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Si la instancia ya existe, la devolvemos.
            // Si no, creamos la base de datos dentro de un bloque 'synchronized'
            // para evitar que se cree más de una vez en entornos multihilo.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventario_database" // Nombre del archivo de la base de datos
                )
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
