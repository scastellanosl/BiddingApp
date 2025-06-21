package com.example.biddingapp.data.model.model

import com.google.gson.annotations.SerializedName

/**
 * Data class para representar los campos que se pueden actualizar en una puja existente.
 * Utilizada para las solicitudes PATCH a /bids/{id}.
 *
 * @param amount El nuevo monto de la puja. Es nulo si no se va a actualizar el monto.
 */
data class BidUpdate(
    @SerializedName("amount") val amount: Double? = null // Solo incluimos el campo que vamos a actualizar
)
