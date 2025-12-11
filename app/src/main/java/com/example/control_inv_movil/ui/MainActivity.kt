package com.example.control_inv_movil.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.control_inv_movil.R // Asegúrate de que esta importación esté
import com.example.control_inv_movil.data.local.AppDatabase
import com.example.control_inv_movil.data.repository.ProductoRepository
import com.example.control_inv_movil.databinding.ActivityMainBinding
import com.example.control_inv_movil.ui.adapter.ProductoAdapter
import com.example.control_inv_movil.ui.viewmodel.ProductoViewModel
import com.example.control_inv_movil.ui.viewmodel.ProductoViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val productoViewModel: ProductoViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ProductoRepository(database.productoDao())
        ProductoViewModelFactory(repository)
    }
    private lateinit var productoAdapter: ProductoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        productoAdapter = ProductoAdapter { producto ->
            val intent = Intent(this, DetalleProductoActivity::class.java)
            intent.putExtra("PRODUCTO_ID", producto.id)
            startActivity(intent)
        }

        binding.recyclerViewProductos.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = productoAdapter
        }

        // La observación de 'todosLosProductos' ahora reaccionará a la búsqueda automáticamente
        productoViewModel.todosLosProductos.observe(this) { productos ->
            productos?.let {
                productoAdapter.submitList(it)
            }
        }

        binding.fabAgregarProducto.setOnClickListener {
            val intent = Intent(this, AddEditProductoActivity::class.java)
            startActivity(intent)
        }
    }

    // --- INICIO: NUEVAS FUNCIONES PARA EL MENÚ DE BÚSQUEDA ---
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu) // Inflamos nuestro menú

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        // Escuchamos los cambios en el texto de búsqueda
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // No necesitamos hacer nada cuando se presiona "Enter",
            // ya que la búsqueda es en tiempo real.
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            // Se llama cada vez que el texto cambia (letra por letra)
            override fun onQueryTextChange(newText: String?): Boolean {
                productoViewModel.setSearchQuery(newText.orEmpty())
                return true
            }
        })

        return true
    }
    // En MainActivity.kt

    // ... (después del final de la función onCreateOptionsMenu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_view_log -> {
                // Si se pulsa el ítem de historial, abrimos LogActivity
                val intent = Intent(this, LogActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // --- FIN: NUEVAS FUNCIONES PARA EL MENÚ DE BÚSQUEDA ---
}
