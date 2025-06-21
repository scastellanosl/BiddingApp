package com.example.biddingapp.data.model.model

import com.google.gson.annotations.SerializedName

/**
 * Data class que representa una puja registrada en la API, incluyendo su ID.
 * Se diferencia de BidRequest en que Bid incluye el 'id' asignado por el servidor,
 * lo cual es necesario para operaciones de actualización (PATCH/PUT).
 *
 * @param id El ID único de la puja, asignado por el servidor (JSON Server).
 * @param amount El monto de la puja.
 * @param userId El ID del usuario que realizó la puja (ahora será el nombre del empleado).
 * @param auctionId El ID de la subasta a la que pertenece esta puja.
 */
data class Bid(
    @SerializedName("id") val id: String, // El ID ahora es no nulo y siempre viene del servidor
    @SerializedName("amount") val amount: Double,
    @SerializedName("user_id") val userId: String, // <-- Este campo almacena el nombre del empleado
    @SerializedName("auction_id") val auctionId: String
)