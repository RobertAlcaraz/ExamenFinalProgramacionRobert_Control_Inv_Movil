package com.example.control_inv_movil.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.control_inv_movil.data.local.model.Producto

/**
 * Objeto de Acceso a Datos para la tabla de productos.
 * Define las operaciones de base de datos que se pueden realizar.
 */
@Dao
interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarios(productos: List<Producto>)

    /**
     * Inserta un producto. Si ya existe, lo reemplaza.
     * Es una función 'suspend' para ser llamada desde una coroutine.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: Producto)

    /**
     * Actualiza un producto existente.
     */
    @Update
    suspend fun actualizar(producto: Producto)

    /**
     * Elimina un producto.
     */
    @Delete
    suspend fun eliminar(producto: Producto)

    /**
     * Obtiene todos los productos ordenados por nombre.
     * Devuelve LiveData, que notificará a los observadores cuando los datos cambien.
     */
    @Query("SELECT * FROM productos WHERE nombre LIKE :searchQuery ORDER BY nombre ASC")
    fun obtenerTodos(searchQuery: String): LiveData<List<Producto>>


    /**
     * Obtiene un producto específico por su ID.
     */
    @Query("SELECT * FROM productos WHERE id = :productoId")
    suspend fun obtenerPorId(productoId: Int): Producto?

    // --- INICIO: NUEVA FUNCIÓN AÑADIDA ---
    /**
     * Obtiene el primer producto de la tabla.
     * Se usa para tener un producto de ejemplo para enviar al webhook.
     */
    @Query("SELECT * FROM productos LIMIT 1")
    suspend fun obtenerPrimerProducto(): Producto?
    // --- FIN: NUEVA FUNCIÓN AÑADIDA ---
}
