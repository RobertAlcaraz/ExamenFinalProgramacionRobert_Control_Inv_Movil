package com.example.control_inv_movil.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Un objeto Singleton que se encarga de crear y configurar la instancia de Retrofit.
 */
object RetrofitClient {

    // La URL base del servidor al que nos conectaremos.
    private const val BASE_URL = "https://webhook.site/5c949c5a-c669-4c21-8730-0657fa9ad4ea/"

    // Creamos un interceptor para poder ver las llamadas de red en el Logcat.
    // Esto es extremadamente útil para depurar y ver qué envía y recibe la app.
    // Lo configuramos en nivel BODY para ver toda la información.
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Creamos un cliente de OkHttp y le añadimos nuestro interceptor de logs.
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    /**
     * Propiedad 'lazy' que crea la instancia de ApiService la primera vez que se accede a ella.
     * Esto asegura que solo se cree una vez en toda la vida de la aplicación.
     */
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL) // 1. Le decimos la URL base.
            .client(client)    // 2. Le asignamos nuestro cliente con logs.
            .addConverterFactory(GsonConverterFactory.create()) // 3. Le decimos que use Gson para convertir JSON a objetos Kotlin.
            .build()

        // 4. Creamos y devolvemos la implementación de nuestra interfaz ApiService.
        retrofit.create(ApiService::class.java)
    }
}
