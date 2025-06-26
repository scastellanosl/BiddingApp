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
// Auction.kt - MODIFIED

data class Auction(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String?, // Make nullable
    @SerializedName("max_offer") val maxOffer: Double,
    @SerializedName("inscriptions") val inscriptions: Int,
    @SerializedName("end_date") val endDate: String?, // Make nullable
    @SerializedName("description") val description: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("min_bid") val minBid: Double? = null,
    @SerializedName("is_active") val isActive: Boolean? = null,
    @SerializedName("winner_id") val winnerId: String? = null,
    @SerializedName("winning_bid") val winningBid: Double? = null
)