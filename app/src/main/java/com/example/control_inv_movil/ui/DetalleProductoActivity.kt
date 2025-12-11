package com.example.control_inv_movil.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast // Importar Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.control_inv_movil.R
import com.example.control_inv_movil.data.local.AppDatabase
import com.example.control_inv_movil.data.local.model.Producto
import com.example.control_inv_movil.data.repository.ProductoRepository
import com.example.control_inv_movil.databinding.ActivityDetalleProductoBinding
import com.example.control_inv_movil.ui.viewmodel.ProductoViewModel
import com.example.control_inv_movil.ui.viewmodel.ProductoViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.util.Locale
import kotlin.text.format

class DetalleProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleProductoBinding
    private var productoActual: Producto? = null
    private var productoId: Int = -1

    private val productoViewModel: ProductoViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ProductoRepository(database.productoDao())
        ProductoViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración del Toolbar
        setSupportActionBar(binding.toolbarDetalle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Obtener el ID del producto
        productoId = intent.getIntExtra("PRODUCTO_ID", -1)
        if (productoId == -1) {
            finish()
            return
        }

        // Cargar los datos del producto
        cargarDatos()

        // --- Lógica de los botones ---
        binding.botonEditar.setOnClickListener {
            val intent = Intent(this, AddEditProductoActivity::class.java)
            intent.putExtra("PRODUCTO_ID", productoId)
            startActivity(intent)
        }

        binding.botonEliminar.setOnClickListener {
            productoActual?.let {
                mostrarDialogoDeConfirmacion(it)
            }
        }

        // --- INICIO: LÓGICA DEL NUEVO BOTÓN DE SINCRONIZACIÓN ---
        binding.botonSincronizar.setOnClickListener {
            productoViewModel.sincronizar()
            Toast.makeText(this, "Iniciando sincronización...", Toast.LENGTH_SHORT).show()
            // La lista en MainActivity se actualizará automáticamente gracias a LiveData
        }
        // --- FIN: LÓGICA DEL NUEVO BOTÓN DE SINCRONIZACIÓN ---
    }

    override fun onResume() {
        super.onResume()
        if(productoId != -1) {
            cargarDatos()
        }
    }

    private fun cargarDatos() {
        lifecycleScope.launch {
            productoActual = productoViewModel.obtenerPorId(productoId)
            productoActual?.let { producto ->
                binding.toolbarDetalle.title = producto.nombre
                binding.textViewNombreDetalle.text = producto.nombre
                binding.textViewDescripcionDetalle.text = producto.descripcion
                binding.textViewCantidadDetalle.text = producto.cantidad.toString()

                val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "PE"))
                binding.textViewPrecioDetalle.text = formatoMoneda.format(producto.precio)

                if (producto.rutaImagen != null) {
                    Glide.with(this@DetalleProductoActivity)
                        .load(File(producto.rutaImagen))
                        .into(binding.imagenDetalleProducto)
                } else {
                    binding.imagenDetalleProducto.setImageResource(R.drawable.ic_camera)
                }
            }
        }
    }

    private fun mostrarDialogoDeConfirmacion(producto: Producto) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar el producto '${producto.nombre}'?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                productoViewModel.eliminar(this, producto)
                finish()
            }
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
