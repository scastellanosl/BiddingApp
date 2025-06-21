// AuctionRepository.kt
package com.example.biddingapp.data.model.repository

import com.example.biddingapp.data.model.model.Auction
import com.example.biddingapp.data.model.model.Bid
import com.example.biddingapp.data.model.model.BidRequest
import com.example.biddingapp.data.model.model.BidResponse
import com.example.biddingapp.data.model.model.BidUpdate
import com.example.biddingapp.data.model.remote.ApiService
import com.example.biddingapp.data.model.remote.NetworkModule

/**
 * AuctionRepository: La capa del repositorio que abstrae la fuente de datos para las subastas.
 */
class AuctionRepository(private val apiService: ApiService) {

    constructor() : this(NetworkModule.apiService)

    suspend fun getAuctions(query: String? = null): List<Auction>? {
        return try {
            val response = apiService.getAuctions(query)
            if (response.isSuccessful) {
                response.body()
            } else {
                println("Error al obtener subastas: ${response.code()} - ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            println("Excepción al obtener subastas: ${e.message}")
            null
        }
    }

    suspend fun getAuctionDetail(auctionId: String): Auction? {
        return try {
            val response = apiService.getAuctionDetail(auctionId)
            if (response.isSuccessful) {
                response.body()
            } else {
                println("Error al obtener detalle de subasta: ${response.code()} - ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            println("Excepción al obtener detalle de subasta: ${e.message}")
            null
        }
    }

    suspend fun placeBid(bidRequest: BidRequest): Boolean {
        return try {
            val response = apiService.placeBid(bidRequest)
            response.isSuccessful
        } catch (e: Exception) {
            println("Excepción al pujar: ${e.message}")
            false
        }
    }

    suspend fun getAuctionResult(auctionId: String): BidResponse? {
        return try {
            val response = apiService.getAuctionResult(auctionId)
            if (response.isSuccessful) {
                response.body()
            } else {
                println("Error al obtener resultado de subasta: ${response.code()} - ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            println("Excepción al obtener resultado de subasta: ${e.message}")
            null
        }
    }

    suspend fun createAuction(auction: Auction): Auction? {
        return try {
            val response = apiService.createAuction(auction)
            if (response.isSuccessful) {
                response.body()
            } else {
                println("Error al crear subasta: ${response.code()} - ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            println("Excepción al crear subasta: ${e.message}")
            null
        }
    }

    suspend fun updateAuction(auctionId: String, auctionUpdates: Auction): Auction? {
        return try {
            val response = apiService.updateAuction(auctionId, auctionUpdates)
            if (response.isSuccessful) {
                response.body()
            } else {
                println("Error al actualizar subasta: ${response.code()} - ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            println("Excepción al actualizar subasta: ${e.message}")
            null
        }
    }

    /**
     * Obtiene todas las pujas para una subasta específica.
     * @param auctionId El ID de la subasta.
     * @return Una lista de Bid (representando las pujas), o null si falla.
     */
    suspend fun getBidsForAuction(auctionId: String): List<Bid>? {
        return try {
            val response = apiService.getBidsForAuction(auctionId)
            if (response.isSuccessful) {
                response.body()
            } else {
                println("Error al obtener pujas para subasta $auctionId: ${response.code()} - ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            println("Excepción al obtener pujas para subasta $auctionId: ${e.message}")
            null
        }
    }

    /**
     * Actualiza el monto de una puja existente.
     * @param bidId El ID de la puja a actualizar.
     * @param newAmount El nuevo monto de la puja.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    suspend fun updateBidAmount(bidId: String, newAmount: Double): Boolean {
        return try {
            val bidUpdate = BidUpdate(amount = newAmount) // <-- ¡Cambiado a BidUpdate!
            println("DEBUG AuctionRepository: Sending PATCH to bids/$bidId with body: $bidUpdate") // <-- DEBUG
            val response = apiService.updateExistingBid(bidId, bidUpdate) // <-- ¡Cambiado a BidUpdate!
            if (!response.isSuccessful) {
                println("DEBUG AuctionRepository: Error updating bid $bidId. Code: ${response.code()}, Body: ${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            println("DEBUG AuctionRepository: Excepción al actualizar puja $bidId: ${e.message}")
            false
        }
    }
}