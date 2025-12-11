package com.example.control_inv_movil.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.control_inv_movil.R
import com.example.control_inv_movil.data.local.AppDatabase
import com.example.control_inv_movil.data.local.model.Producto
import com.example.control_inv_movil.data.repository.ProductoRepository
import com.example.control_inv_movil.databinding.ActivityAddEditProductoBinding
import com.example.control_inv_movil.ui.viewmodel.ProductoViewModel
import com.example.control_inv_movil.ui.viewmodel.ProductoViewModelFactory
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class AddEditProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditProductoBinding

    private val productoViewModel: ProductoViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ProductoRepository(database.productoDao())
        ProductoViewModelFactory(repository)
    }

    private var rutaImagenActual: String? = null
    private var productoId: Int = -1
    // --- INICIO: NUEVA VARIABLE PARA GUARDAR EL ESTADO ORIGINAL ---
    private var productoOriginal: Producto? = null
    // --- FIN: NUEVA VARIABLE ---

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            binding.imagenProducto.setImageBitmap(bitmap)
            rutaImagenActual = guardarImagenEnAlmacenamientoInterno(bitmap)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        productoId = intent.getIntExtra("PRODUCTO_ID", -1)

        if (productoId != -1) {
            binding.toolbar.title = "Editar Producto"
            cargarDatosDelProducto()
        }

        binding.buttonGuardar.setOnClickListener {
            guardarProducto()
        }

        binding.botonTomarFoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraLauncher.launch(null)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun cargarDatosDelProducto() {
        lifecycleScope.launch {
            // Guardamos el producto original en nuestra nueva variable
            productoOriginal = productoViewModel.obtenerPorId(productoId)
            productoOriginal?.let { producto ->
                binding.editTextNombre.setText(producto.nombre)
                binding.editTextDescripcion.setText(producto.descripcion)
                binding.editTextCantidad.setText(producto.cantidad.toString())
                binding.editTextPrecio.setText(producto.precio.toString())

                rutaImagenActual = producto.rutaImagen
                if (rutaImagenActual != null) {
                    Glide.with(this@AddEditProductoActivity)
                        .load(File(rutaImagenActual!!))
                        .into(binding.imagenProducto)
                } else {
                    binding.imagenProducto.setImageResource(R.drawable.ic_camera)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun guardarImagenEnAlmacenamientoInterno(bitmap: Bitmap): String? {
        val directorio = applicationContext.getDir("imagenes", Context.MODE_PRIVATE)
        val nombreArchivo = "IMG_${UUID.randomUUID()}.jpg"
        val archivo = File(directorio, nombreArchivo)
        try {
            val fileOutputStream = FileOutputStream(archivo)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream)
            fileOutputStream.close()
            return archivo.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    private fun guardarProducto() {
        val nombre = binding.editTextNombre.text.toString().trim()
        val descripcion = binding.editTextDescripcion.text.toString().trim()
        val cantidadStr = binding.editTextCantidad.text.toString().trim()
        val precioStr = binding.editTextPrecio.text.toString().trim()

        if (nombre.isEmpty() || descripcion.isEmpty() || cantidadStr.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val cantidad = cantidadStr.toInt()
            val precio = precioStr.toDouble()

            val productoActualizado = Producto(
                id = if (productoId != -1) productoId else 0,
                nombre = nombre,
                descripcion = descripcion,
                cantidad = cantidad,
                precio = precio,
                rutaImagen = rutaImagenActual
            )

            if (productoId == -1) {
                // Modo CREAR: el log sigue siendo simple
                productoViewModel.insertar(applicationContext, productoActualizado)
                Toast.makeText(this, "Producto guardado", Toast.LENGTH_SHORT).show()
            } else {
                // Modo ACTUALIZAR: construimos el log detallado
                val logDetallado = generarLogDeCambios(productoOriginal, productoActualizado)
                productoViewModel.actualizar(applicationContext, productoActualizado, logDetallado)
                Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
            }

            finish()

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Por favor, introduce un número válido para cantidad y precio", Toast.LENGTH_SHORT).show()
        }
    }

    // --- INICIO: NUEVA FUNCIÓN PARA GENERAR EL LOG DETALLADO ---
    private fun generarLogDeCambios(original: Producto?, actualizado: Producto): String {
        if (original == null) return "Producto ACTUALIZADO: ${actualizado.nombre}"

        val cambios = mutableListOf<String>()
        if (original.nombre != actualizado.nombre) {
            cambios.add("Nombre: '${original.nombre}' -> '${actualizado.nombre}'")
        }
        if (original.descripcion != actualizado.descripcion) {
            cambios.add("Descripción: '${original.descripcion}' -> '${actualizado.descripcion}'")
        }
        if (original.cantidad != actualizado.cantidad) {
            cambios.add("Cantidad: ${original.cantidad} -> ${actualizado.cantidad}")
        }
        if (original.precio != actualizado.precio) {
            cambios.add("Precio: ${original.precio} -> ${actualizado.precio}")
        }
        if (original.rutaImagen != actualizado.rutaImagen) {
            cambios.add("Imagen actualizada")
        }

        return if (cambios.isEmpty()) {
            "Producto revisado sin cambios: ${actualizado.nombre} (ID: ${actualizado.id})"
        } else {
            "Producto ACTUALIZADO: ${actualizado.nombre} (ID: ${actualizado.id}) -> Cambios: ${cambios.joinToString(", ")}"
        }
    }
    // --- FIN: NUEVA FUNCIÓN ---
}
