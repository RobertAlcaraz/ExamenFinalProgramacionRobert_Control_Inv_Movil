package com.example.control_inv_movil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.control_inv_movil.data.repository.ProductoRepository

/**
 * Factory para crear instancias de ProductoViewModel con un parámetro en el constructor (el repositorio).
 */
class ProductoViewModelFactory(private val repository: ProductoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Comprueba si la clase que se pide crear es ProductoViewModel
        if (modelClass.isAssignableFrom(ProductoViewModel::class.java)) {
            // Si lo es, crea y devuelve una instancia, haciendo un cast seguro.
            @Suppress("UNCHECKED_CAST")
            return ProductoViewModel(repository) as T
        }
        // Si no es la clase esperada, lanza una excepción.
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
