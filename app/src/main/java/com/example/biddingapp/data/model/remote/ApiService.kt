package com.example.biddingapp.data.model.remote

import com.example.biddingapp.data.model.model.Auction
import com.example.biddingapp.data.model.model.Bid
import com.example.biddingapp.data.model.model.BidRequest
import com.example.biddingapp.data.model.model.BidResponse
import com.example.biddingapp.data.model.model.BidUpdate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * ApiService: Interfaz de Retrofit para definir los endpoints de la API de subastas.
 */
interface ApiService {
    @DELETE("auctions/{auctionId}")
    suspend fun deleteAuction(@Path("auctionId") auctionId: String): Response<Void>


    @GET("auctions")
    suspend fun getAuctions(@Query("search") query: String? = null): Response<List<Auction>>

    @GET("auctions/{id}")
    suspend fun getAuctionDetail(@Path("id") auctionId: String): Response<Auction>

    @POST("bids")
    suspend fun placeBid(@Body bidRequest: BidRequest): Response<Map<String, Any>>

    @GET("auctions/{id}/result")
    suspend fun getAuctionResult(@Path("id") auctionId: String): Response<BidResponse>

    @POST("auctions")
    suspend fun createAuction(@Body auction: Auction): Response<Auction>

    @PATCH("auctions/{id}")
    suspend fun updateAuction(@Path("id") auctionId: String, @Body auctionUpdates: Auction): Response<Auction>

    // --- Add this line for getBidsForAuction ---
    @GET("bids")
    suspend fun getBidsForAuction(@Query("auctionId") auctionId: String): Response<List<Bid>>

    @PATCH("bids/{id}")
    suspend fun updateExistingBid(@Path("id") bidId: String, @Body updates: BidUpdate): Response<Bid>
}