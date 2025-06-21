// CreateAuctionViewModel.kt
package com.example.biddingapp.viewmodel // Asegúrate de esta importación

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biddingapp.data.model.model.Auction
import com.example.biddingapp.data.model.repository.AuctionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * CreateAuctionViewModel: ViewModel para la pantalla de creación de una nueva subasta.
 * Gestiona el estado de los campos de entrada y la lógica para crear una subasta.
 */
class CreateAuctionViewModel(private val repository: AuctionRepository = AuctionRepository()) : ViewModel() {

    // Estados para los campos del formulario
    private val _auctionName = MutableStateFlow("")
    val auctionName: StateFlow<String> = _auctionName

    private val _minimumOffer = MutableStateFlow("")
    val minimumOffer: StateFlow<String> = _minimumOffer

    private val _selectedDate = MutableStateFlow("") // <-- Estado para la fecha seleccionada
    val selectedDate: StateFlow<String> = _selectedDate

    private val _imageUrl = MutableStateFlow("") // <-- Estado para la URL de la imagen
    val imageUrl: StateFlow<String> = _imageUrl

    // Estados para la UI (carga, errores, éxito)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    // Funciones para actualizar los estados desde la UI
    fun updateAuctionName(name: String) {
        _auctionName.value = name
    }

    fun updateMinimumOffer(offer: String) {
        _minimumOffer.value = offer
    }

    fun updateSelectedDate(date: String) { // <-- ¡NUEVA FUNCIÓN PARA ACTUALIZAR LA FECHA!
        _selectedDate.value = date
    }

    fun updateImageUrl(url: String) { // <-- NUEVA FUNCIÓN PARA ACTUALIZAR LA URL DE IMAGEN
        _imageUrl.value = url
    }

    /**
     * Intenta crear una nueva subasta utilizando los datos del formulario.
     * @param onAuctionCreated Callback para ejecutar cuando la subasta es creada exitosamente.
     */
    fun createAuction(onAuctionCreated: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            // Validaciones básicas antes de enviar a la API
            val name = _auctionName.value
            val minOffer = _minimumOffer.value.toDoubleOrNull()
            val endDate = _selectedDate.value
            val imgUrl = _imageUrl.value.ifEmpty { null } // Si está vacía, que sea null

            if (name.isBlank()) {
                _errorMessage.value = "El nombre de la subasta no puede estar vacío."
                _isLoading.value = false
                return@launch
            }
            if (minOffer == null || minOffer <= 0) {
                _errorMessage.value = "La oferta mínima debe ser un número válido y mayor que cero."
                _isLoading.value = false
                return@launch
            }
            if (endDate.isBlank()) {
                _errorMessage.value = "Debes seleccionar una fecha final para la subasta."
                _isLoading.value = false
                return@launch
            }

            // Crea el objeto Auction para enviar a la API
            val newAuction = Auction(
                id = null, // El ID lo genera el servidor
                name = name,
                description = "Subasta de $name", // Puedes añadir un campo de descripción en la UI si quieres
                maxOffer = minOffer, // Inicialmente la oferta máxima es la oferta mínima
                inscriptions = 0,
                endDate = endDate,
                imageUrl = imgUrl,
                minBid = minOffer,
                isActive = true, // Por defecto, una nueva subasta está activa
                winnerId = null,
                winningBid = null
            )

            val createdAuction = repository.createAuction(newAuction)

            if (createdAuction != null) {
                _successMessage.value = "Subasta '${createdAuction.name}' creada con éxito."
                // Limpiar campos después de éxito (opcional)
                _auctionName.value = ""
                _minimumOffer.value = ""
                _selectedDate.value = ""
                _imageUrl.value = ""
                // Llama al callback para navegar de vuelta
                onAuctionCreated()
            } else {
                _errorMessage.value = "Error al crear la subasta. Inténtalo de nuevo."
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
}
