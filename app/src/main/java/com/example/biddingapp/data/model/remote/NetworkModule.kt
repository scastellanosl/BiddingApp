package com.example.biddingapp.data.model.remote

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * NetworkModule: Objeto para configurar y proveer instancias de Retrofit y OkHttpClient.
 * Esto ayuda a mantener la configuración de red centralizada.
 */
object NetworkModule {

    private const val BASE_URL = "http://192.168.122.1:3000/" // ¡¡IMPORTANTE!! Reemplaza con la URL de tu API real

    // Configuración del interceptor de logging para ver las solicitudes y respuestas en Logcat.
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Nivel de detalle del log (BASIC, HEADERS, BODY)
    }

    // Configuración del cliente OkHttpClient.
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // Añade el interceptor de logging para debug
        .connectTimeout(30, TimeUnit.SECONDS) // Tiempo máximo para establecer la conexión
        .readTimeout(30, TimeUnit.SECONDS) // Tiempo máximo para leer los datos
        .writeTimeout(30, TimeUnit.SECONDS) // Tiempo máximo para escribir los datos
        .build()

    // Configuración del objeto Gson.
    private val gson = GsonBuilder()
        .setLenient() // Permite JSON que no sea estrictamente conforme a RFC 4627
        .create()

    // Configuración de Retrofit.
    // Usa 'lazy' para que la instancia se cree solo cuando se necesite por primera vez.
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // La URL base de tu API
            .client(okHttpClient) // El cliente HTTP configurado
            .addConverterFactory(GsonConverterFactory.create(gson)) // Convertidor para JSON (Gson)
            .build()
    }

    // Servicio de la API de subastas.
    // 'lazy' también para la instancia del servicio.
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
