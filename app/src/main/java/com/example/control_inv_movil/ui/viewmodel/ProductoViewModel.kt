package com.example.control_inv_movil.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.control_inv_movil.data.local.model.Producto
import com.example.control_inv_movil.data.repository.ProductoRepository
import com.example.control_inv_movil.utils.AppLogger
import kotlinx.coroutines.launch

/**
 * ViewModel para la gestión de productos.
 * Provee datos a la UI y maneja las acciones del usuario.
 * @param repository El repositorio de productos.
 */
class ProductoViewModel(private val repository: ProductoRepository) : ViewModel() {

    // --- Lógica para la Búsqueda ---
    private val _searchQuery = MutableLiveData<String>("")

    val todosLosProductos: LiveData<List<Producto>> = _searchQuery.switchMap { query ->
        repository.obtenerTodos("%$query%")
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // --- INICIO: NUEVA FUNCIÓN PARA SINCRONIZAR ---
    /**
     * Inicia una coroutina para llamar a la función de sincronización del repositorio.
     */
    fun sincronizar() = viewModelScope.launch {
        repository.sincronizarProductos()
        // Opcional: podrías añadir un log aquí para indicar que el proceso de sincronización ha comenzado.
    }
    // --- FIN: NUEVA FUNCIÓN PARA SINCRONIZAR ---

    // --- Funciones del CRUD con Logger ---

    /**
     * Inserta un producto y registra un log simple.
     */
    fun insertar(context: Context, producto: Producto) = viewModelScope.launch {
        repository.insertar(producto)
        AppLogger.log(context, "CRUD", "Producto CREADO: ${producto.nombre}")
    }

    /**
     * Actualiza un producto y registra un log detallado que se le pasa como parámetro.
     */
    fun actualizar(context: Context, producto: Producto, logDetallado: String) = viewModelScope.launch {
        repository.actualizar(producto)
        // Usamos el mensaje detallado que nos pasan desde la Activity
        AppLogger.log(context, "CRUD", logDetallado)
    }

    /**
     * Elimina un producto y registra un log simple.
     */
    fun eliminar(context: Context, producto: Producto) = viewModelScope.launch {
        repository.eliminar(producto)
        AppLogger.log(context, "CRUD", "Producto ELIMINADO: ${producto.nombre} (ID: ${producto.id})")
    }

    /**
     * Obtiene un solo producto por su ID.
     */
    suspend fun obtenerPorId(id: Int): Producto? {
        return repository.obtenerPorId(id)
    }
}
