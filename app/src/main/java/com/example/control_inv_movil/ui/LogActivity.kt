package com.example.control_inv_movil.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.control_inv_movil.databinding.ActivityLogBinding
import java.io.File

class LogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el Toolbar
        setSupportActionBar(binding.toolbarLog)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Muestra la flecha de "Atrás"

        // Cargar y mostrar los logs
        leerYMostrarLogs()
    }

    private fun leerYMostrarLogs() {
        val nombreArchivoLog = "inventario_log.txt"
        val archivoLog = File(filesDir, nombreArchivoLog)

        if (archivoLog.exists()) {
            // Si el archivo existe, leemos todo su contenido
            val contenido = archivoLog.readText()
            binding.textViewLogContent.text = contenido
        } else {
            // Si el archivo no existe, mostramos un mensaje
            binding.textViewLogContent.text = "Aún no se han registrado eventos."
        }
    }

    // Para manejar el clic en la flecha de "Atrás" del Toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish() // Cierra la actividad actual y vuelve a la anterior
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
