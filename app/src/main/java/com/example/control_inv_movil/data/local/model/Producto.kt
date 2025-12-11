package com.example.control_inv_movil.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa la tabla 'productos' en la base de datos.
 * Cada instancia de esta clase es una fila en la tabla.
 */
@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Clave primaria que se autoincrementa

    val nombre: String,
    val descripcion: String,
    val cantidad: Int,
    val precio: Double,

    // Guardará la ruta local del archivo de imagen. Puede ser nulo.
    val rutaImagen: String? = null
)
