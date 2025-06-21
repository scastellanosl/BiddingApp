package com.example.biddingapp.data.model.model


import com.google.gson.annotations.SerializedName

/**
 * Data class para representar la respuesta de una operación de puja o resultado de subasta.
 * @param success Indica si la operación fue exitosa.
 * @param message Un mensaje descriptivo (ej: "Puja registrada", "Subasta finalizada").
 * @param newMaxOffer La nueva oferta máxima, si aplica.
 * @param winnerId El ID del ganador de la subasta, si aplica.
 * @param winningBid La puja ganadora, si aplica.
 */
data class BidResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("new_max_offer") val newMaxOffer: Double? = null,
    @SerializedName("winner_id") val winnerId: String? = null,
    @SerializedName("winning_bid") val winningBid: Double? = null
)