package com.example.control_inv_movil.ui.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.control_inv_movil.R
import com.example.control_inv_movil.data.local.model.Producto
import com.example.control_inv_movil.databinding.ItemProductoBinding
import java.io.File

// --- INICIO: CAMBIO EN EL CONSTRUCTOR ---
// Ahora el adaptador acepta una función lambda para manejar los clics.
class ProductoAdapter(
    private val onItemClicked: (Producto) -> Unit
) : ListAdapter<Producto, ProductoAdapter.ProductoViewHolder>(DiffCallback()) {
// --- FIN: CAMBIO EN EL CONSTRUCTOR ---

    inner class ProductoViewHolder(private val binding: ItemProductoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(producto: Producto) {
            binding.textViewNombreProducto.text = producto.nombre
            binding.textViewDescripcionProducto.text = producto.descripcion
            binding.textViewCantidad.text = producto.cantidad.toString()

            if (producto.rutaImagen != null) {
                Glide.with(binding.root.context)
                    .load(File(producto.rutaImagen))
                    .into(binding.imagenProductoItem)
            } else {
                Glide.with(binding.root.context)
                    .load(R.drawable.ic_camera)
                    .into(binding.imagenProductoItem)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val productoActual = getItem(position)
        holder.bind(productoActual)

        // --- INICIO: NUEVA LÓGICA PARA EL CLIC ---
        // Asignamos el listener al 'itemView', que es toda la tarjeta.
        // Cuando se pulsa, se llama a la función que nos pasaron en el constructor.
        holder.itemView.setOnClickListener {
            onItemClicked(productoActual)
        }
        // --- FIN: NUEVA LÓGICA PARA EL CLIC ---
    }
}
