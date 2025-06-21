package com.example.biddingapp.data.model.model

// Auction.kt

import com.google.gson.annotations.SerializedName

/**
 * Data class que representa un objeto de subasta.
 * Se incluyen anotaciones @SerializedName si los nombres de las propiedades JSON
 * difieren de los nombres de las propiedades Kotlin.
 * Por ahora, reutilizamos la clase Auction existente. Si tu API tiene IDs u otros campos,
 * los añadiríamos aquí.
 */
data class Auction(
    @SerializedName("id") val id: String? = null, // Añadido un ID, común en APIs
    @SerializedName("name") val name: String,
    @SerializedName("max_offer") val maxOffer: Double, // Ejemplo si la API usa snake_case
    @SerializedName("inscriptions") val inscriptions: Int,
    @SerializedName("end_date") val endDate: String,
    @SerializedName("description") val description: String? = null, // Posible campo adicional para detalle
    @SerializedName("image_url") val imageUrl: String? = null, // URL de la imagen del item
    @SerializedName("min_bid") val minBid: Double? = null, // Oferta mínima para crear subasta
    @SerializedName("is_active") val isActive: Boolean? = null, // Estado de la subasta
    @SerializedName("winner_id") val winnerId: String? = null, // ID del ganador si ha terminado
    @SerializedName("winning_bid") val winningBid: Double? = null // Puja ganadora si ha terminado
)