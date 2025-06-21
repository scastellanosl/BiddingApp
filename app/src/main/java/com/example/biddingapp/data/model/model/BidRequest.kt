package com.example.biddingapp.data.model.model

import com.google.gson.annotations.SerializedName

/**
 * Data class para representar el cuerpo de una solicitud de puja.
 * Ahora incluye el ID de la subasta a la que se refiere la puja.
 * @param amount El monto de la puja.
 * @param userId El ID del usuario que realiza la puja.
 * @param auctionId El ID de la subasta a la que se le realiza la puja.
 */
data class BidRequest(
    @SerializedName("amount") val amount: Double,
    @SerializedName("user_id") val userId: String, //
    @SerializedName("auction_id") val auctionId: String
)
