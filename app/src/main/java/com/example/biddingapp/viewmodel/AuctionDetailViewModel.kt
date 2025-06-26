// AuctionDetailViewModel.kt
package com.example.biddingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biddingapp.data.model.model.Auction
import com.example.biddingapp.data.model.model.Bid
import com.example.biddingapp.data.model.model.BidRequest
import com.example.biddingapp.data.model.repository.AuctionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * AuctionDetailViewModel: ViewModel para la pantalla de detalle de una subasta.
 */
class AuctionDetailViewModel(private val repository: AuctionRepository = AuctionRepository()) : ViewModel() {

    private val _auctionDetail = MutableStateFlow<Auction?>(null)
    val auctionDetail: StateFlow<Auction?> = _auctionDetail

    private val _bidsForAuction = MutableStateFlow<List<Bid>>(emptyList())
    val bidsForAuction: StateFlow<List<Bid>> = _bidsForAuction

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    // --- Nuevo StateFlow para la navegación después de la eliminación ---
    private val _isAuctionDeleted = MutableStateFlow(false)
    val isAuctionDeleted: StateFlow<Boolean> = _isAuctionDeleted


    /**
     * Carga los detalles de una subasta específica y sus pujas.
     * @param auctionId El ID de la subasta a cargar.
     */
    fun fetchAuctionDetail(auctionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            val fetchedAuction = repository.getAuctionDetail(auctionId)
            if (fetchedAuction != null) {
                _auctionDetail.value = fetchedAuction
                fetchBidsForAuction(auctionId)
            } else {
                _errorMessage.value = "Error al cargar detalles de la subasta."
            }
            _isLoading.value = false
        }
    }

    /**
     * Obtiene las pujas para una subasta específica y las ordena por monto descendente.
     * @param auctionId El ID de la subasta.
     */
    private suspend fun fetchBidsForAuction(auctionId: String) {
        val fetchedBids = repository.getBidsForAuction(auctionId)
        if (fetchedBids != null) {
            _bidsForAuction.value = fetchedBids.sortedByDescending { it.amount }
        } else {
            _bidsForAuction.value = emptyList()
        }
    }


    /**
     * Envía una puja para la subasta actual (crea una nueva puja).
     * @param auctionId El ID de la subasta.
     * @param amount El monto de la puja.
     * @param userName El nombre del usuario que realiza la puja.
     */
    fun placeBid(auctionId: String, amount: Double, userName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            val bidRequest = BidRequest(amount = amount, userId = userName, auctionId = auctionId)
            val isBidSuccessful = repository.placeBid(bidRequest)

            if (isBidSuccessful) {
                fetchBidsForAuction(auctionId)
                val currentMaxOfferInBids = _bidsForAuction.value.firstOrNull()?.amount ?: 0.0

                val currentAuction = _auctionDetail.value
                val currentInscriptions = currentAuction?.inscriptions ?: 0

                val auctionUpdates = Auction(
                    id = auctionId,
                    name = currentAuction?.name ?: "",
                    maxOffer = currentMaxOfferInBids,
                    inscriptions = currentInscriptions + 1,
                    endDate = currentAuction?.endDate ?: "",
                    description = currentAuction?.description,
                    imageUrl = currentAuction?.imageUrl,
                    minBid = currentAuction?.minBid,
                    isActive = currentAuction?.isActive,
                    winnerId = currentAuction?.winnerId,
                    winningBid = currentAuction?.winningBid
                )

                val updatedAuction = repository.updateAuction(auctionId, auctionUpdates)

                if (updatedAuction != null) {
                    _auctionDetail.value = updatedAuction
                    _successMessage.value = "Puja registrada y subasta actualizada con éxito."
                } else {
                    _errorMessage.value = "Puja registrada, pero hubo un error al actualizar la subasta principal."
                    fetchAuctionDetail(auctionId)
                }

            } else {
                _errorMessage.value = "Error al registrar la puja. Inténtalo de nuevo."
            }
            _isLoading.value = false
        }
    }

    /**
     * Actualiza el monto de una puja existente.
     * @param bidId El ID de la puja a actualizar.
     * @param newAmountString El nuevo monto de la puja como String (para validar y convertir).
     */
    fun updateBidAmount(bidId: String, newAmountString: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            val newAmount = newAmountString.toDoubleOrNull()
            if (newAmount == null || newAmount <= 0) {
                _errorMessage.value = "El monto debe ser un número válido y mayor que cero."
                _isLoading.value = false
                return@launch
            }

            val isUpdateSuccessful = repository.updateBidAmount(bidId, newAmount)

            if (isUpdateSuccessful) {
                _successMessage.value = "Puja actualizada con éxito."

                _auctionDetail.value?.id?.let { auctionId ->
                    fetchBidsForAuction(auctionId)
                }

                val newMaxOffer = _bidsForAuction.value.firstOrNull()?.amount

                val currentAuction = _auctionDetail.value
                if (currentAuction != null && newMaxOffer != null && newMaxOffer > currentAuction.maxOffer) {
                    val auctionUpdates = Auction(
                        id = currentAuction.id,
                        name = currentAuction.name,
                        maxOffer = newMaxOffer,
                        inscriptions = currentAuction.inscriptions,
                        endDate = currentAuction.endDate,
                        description = currentAuction.description,
                        imageUrl = currentAuction.imageUrl,
                        minBid = currentAuction.minBid,
                        isActive = currentAuction.isActive,
                        winnerId = currentAuction.winnerId,
                        winningBid = currentAuction.winningBid
                    )
                    currentAuction.id?.let { id ->
                        repository.updateAuction(id, auctionUpdates)
                    }
                }
                _auctionDetail.value?.id?.let { auctionId ->
                    fetchAuctionDetail(auctionId)
                }

            } else {
                _errorMessage.value = "Error al actualizar la puja."
            }
            _isLoading.value = false
        }
    }


    /**
     * Finaliza la subasta, determina el ganador y actualiza el servidor.
     * @param auctionId El ID de la subasta a finalizar.
     */
    fun finishAuction(auctionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            val currentAuction = _auctionDetail.value

            val bids = repository.getBidsForAuction(auctionId)

            var winnerName: String? = null
            var winningBid: Double? = null

            if (!bids.isNullOrEmpty()) {
                val highestBid = bids.maxByOrNull { it.amount }
                winnerName = highestBid?.userId
                winningBid = highestBid?.amount
            }

            val auctionUpdates = Auction(
                id = auctionId,
                name = currentAuction?.name ?: "",
                maxOffer = winningBid ?: (currentAuction?.maxOffer ?: 0.0),
                inscriptions = currentAuction?.inscriptions ?: 0,
                endDate = currentAuction?.endDate ?: "",
                description = currentAuction?.description,
                imageUrl = currentAuction?.imageUrl,
                minBid = currentAuction?.minBid,
                isActive = false,
                winnerId = winnerName,
                winningBid = winningBid
            )

            val updatedAuction = repository.updateAuction(auctionId, auctionUpdates)

            if (updatedAuction != null) {
                _auctionDetail.value = updatedAuction
                _successMessage.value = "Subasta finalizada con éxito. Ganador: ${winnerName ?: "N/A"} con $${winningBid ?: "N/A"}."
            } else {
                _errorMessage.value = "Error al finalizar la subasta. Inténtalo de nuevo."
                fetchAuctionDetail(auctionId)
            }
            _isLoading.value = false
        }
    }

    /**
     * Función pública para limpiar el mensaje de éxito desde la UI.
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    /**
     * Función pública para limpiar el mensaje de error desde la UI.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Elimina una subasta del servidor.
     * @param auctionId El ID de la subasta a eliminar.
     */
    fun deleteAuction(auctionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            _isAuctionDeleted.value = false // Reinicia el estado antes de la operación

            val isDeleted = repository.deleteAuction(auctionId) // Llama a la función real del repositorio

            if (isDeleted) {
                _successMessage.value = "Subasta eliminada con éxito."
                _isAuctionDeleted.value = true // ¡Esto es clave para la navegación!
            } else {
                _errorMessage.value = "Error al eliminar la subasta. Inténtalo de nuevo."
            }
            _isLoading.value = false
        }
    }
}