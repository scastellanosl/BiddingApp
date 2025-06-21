// AuctionDetailViewModel.kt
package com.example.biddingapp.ui.viewmodel

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
            // No mostrar error aquí para no sobrescribir errores de carga de la subasta principal.
            // Los logs de consola en el repositorio ya dan suficiente información.
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
            val isBidSuccessful = repository.placeBid(bidRequest) // Registra la puja en la colección 'bids'

            if (isBidSuccessful) {
                // Recargamos las pujas para obtener la nueva y luego recalculamos el máximo
                fetchBidsForAuction(auctionId)
                val currentMaxOfferInBids = _bidsForAuction.value.firstOrNull()?.amount ?: 0.0 // La primera es la más alta

                val currentInscriptions = _auctionDetail.value?.inscriptions ?: 0

                val auctionUpdates = Auction(
                    id = auctionId,
                    name = "",
                    maxOffer = currentMaxOfferInBids, // Usamos la máxima real de las pujas
                    inscriptions = currentInscriptions + 1, // Incrementamos los inscritos
                    endDate = "",
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
                // Después de actualizar la puja, necesitamos:
                // 1. Recargar todas las pujas para obtener la lista actualizada y ordenada.
                // 2. Determinar la nueva oferta máxima de la subasta a partir de esa lista.
                // 3. Persistir esa nueva oferta máxima en la subasta principal en el servidor.
                fetchBidsForAuction(_auctionDetail.value?.id!!) // Recarga las pujas
                val newMaxOffer = _bidsForAuction.value.firstOrNull()?.amount // La primera es la más alta

                if (newMaxOffer != null && newMaxOffer > (_auctionDetail.value?.maxOffer ?: 0.0)) {
                    val auctionUpdates = Auction(
                        id = _auctionDetail.value?.id,
                        name = "", // No se actualiza
                        maxOffer = newMaxOffer, // <-- ¡Actualizamos la oferta máxima de la subasta principal!
                        inscriptions = _auctionDetail.value?.inscriptions ?: 0, // No se actualiza
                        endDate = "", // No se actualiza
                        // Los demás campos son nulos para que Gson no los incluya en el PATCH
                    )
                    repository.updateAuction(_auctionDetail.value?.id!!, auctionUpdates)
                }
                // Finalmente, recargamos los detalles de la subasta para que la UI se actualice completamente
                fetchAuctionDetail(_auctionDetail.value?.id!!)

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

            // 1. Obtener todas las pujas para esta subasta
            val bids = repository.getBidsForAuction(auctionId)

            var winnerName: String? = null
            var winningBid: Double? = null

            if (!bids.isNullOrEmpty()) {
                // 2. Determinar la puja más alta (el ganador)
                val highestBid = bids.maxByOrNull { it.amount }
                winnerName = highestBid?.userId
                winningBid = highestBid?.amount
            }

            // 3. Crear un objeto Auction con los campos a actualizar para la finalización
            val auctionUpdates = Auction(
                id = auctionId,
                name = "",
                maxOffer = winningBid ?: (_auctionDetail.value?.maxOffer ?: 0.0),
                inscriptions = _auctionDetail.value?.inscriptions ?: 0,
                endDate = "",
                isActive = false,
                winnerId = winnerName,
                winningBid = winningBid
            )

            // 4. Enviar la actualización a la API
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

    fun deleteAuction(auctionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            _successMessage.value = "Subasta eliminada (simulado)."
            _isLoading.value = false
        }
    }
}
