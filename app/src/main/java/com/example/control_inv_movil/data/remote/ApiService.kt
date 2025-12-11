package com.example.control_inv_movil.data.remote

import com.example.control_inv_movil.data.local.model.Producto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interfaz que Retrofit usará para definir las llamadas a la API.
 */
interface ApiService {
    /**
     * Realiza una petición POST a la URL raíz del webhook.
     * @param producto El objeto Producto que se convertirá a JSON y se enviará en el cuerpo de la petición.
     */
    @POST(".") // Usamos @POST y "." para enviar a la URL base.
    suspend fun enviarProducto(@Body producto: Producto): Response<Void>
    // @Body le dice a Retrofit que convierta el objeto 'producto' a JSON.
    // Response<Void> porque webhook.site no devuelve nada específico, solo confirma la recepción.
}
