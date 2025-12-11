package com.example.control_inv_movil.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.control_inv_movil.data.local.dao.ProductoDao
import com.example.control_inv_movil.data.local.model.Producto
import com.example.control_inv_movil.data.remote.ApiService
import com.example.control_inv_movil.data.remote.RetrofitClient
import kotlin.random.Random

/**
 * Repositorio que ahora maneja datos locales (Room) y remotos (Retrofit).
 * @param productoDao El DAO para la base de datos local.
 */
class ProductoRepository(private val productoDao: ProductoDao) {

    // Obtenemos la instancia de nuestro servicio de API desde el RetrofitClient
    private val apiService: ApiService = RetrofitClient.instance

    // Esta función no cambia, sigue obteniendo datos de la BD local
    fun obtenerTodos(searchQuery: String): LiveData<List<Producto>> {
        return productoDao.obtenerTodos(searchQuery)
    }

    // --- INICIO: FUNCIÓN DE SINCRONIZACIÓN MODIFICADA PARA ENVIAR (POST) ---
    /**
     * Toma el primer producto de la base de datos local y lo envía al webhook.
     */
    suspend fun sincronizarProductos() {
        try {
            // 1. Obtenemos el primer producto de la base de datos (o cualquiera para el ejemplo).
            val primerProducto = productoDao.obtenerPrimerProducto()

            if (primerProducto != null) {
                // 2. Hacemos la llamada a la API para ENVIAR el producto.
                val response = apiService.enviarProducto(primerProducto)

                // 3. Verificamos si la llamada fue exitosa (código 2xx).
                if (response.isSuccessful) {
                    Log.d("Sync", "Producto '${primerProducto.nombre}' enviado exitosamente al webhook.")
                } else {
                    Log.e("Sync", "Error al enviar el producto. Código: ${response.code()}")
                }
            } else {
                Log.d("Sync", "No hay productos en la base de datos para enviar.")
            }
        } catch (e: Exception) {
            // 4. Capturamos cualquier error de red.
            Log.e("Sync", "Error al sincronizar (POST): ${e.message}", e)
        }
    }
    // --- FIN: FUNCIÓN DE SINCRONIZACIÓN MODIFICADA ---

    // Las siguientes funciones no cambian, siguen operando sobre la BD local
    suspend fun insertar(producto: Producto) {
        productoDao.insertar(producto)
    }

    suspend fun actualizar(producto: Producto) {
        productoDao.actualizar(producto)
    }

    suspend fun eliminar(producto: Producto) {
        productoDao.eliminar(producto)
    }

    suspend fun obtenerPorId(id: Int): Producto? {
        return productoDao.obtenerPorId(id)
    }
}
