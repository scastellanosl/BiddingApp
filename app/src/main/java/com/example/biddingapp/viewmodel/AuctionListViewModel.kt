package com.example.biddingapp.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biddingapp.data.model.model.Auction
import com.example.biddingapp.data.model.repository.AuctionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * AuctionListViewModel: ViewModel para la pantalla de lista de subastas.
 * Gestiona la lógica de negocio y el estado de la UI para mostrar y buscar subastas.
 *
 * @param repository El repositorio de subastas para acceder a los datos.
 */
class AuctionListViewModel(private val repository: AuctionRepository = AuctionRepository()) : ViewModel() {

    // MutableStateFlow para exponer la lista de subastas a la UI.
    // Se inicializa como una lista vacía.
    private val _auctions = MutableStateFlow<List<Auction>>(emptyList())
    val auctions: StateFlow<List<Auction>> = _auctions // Inmutable para la UI

    // MutableStateFlow para exponer el estado de carga.
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading // Inmutable para la UI

    // MutableStateFlow para exponer mensajes de error.
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage // Inmutable para la UI

    // MutableStateFlow para el término de búsqueda actual.
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        // Al inicializar el ViewModel, cargamos las subastas iniciales.
        fetchAuctions()
    }

    /**
     * Actualiza el término de búsqueda.
     * @param query El nuevo término de búsqueda.
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Realiza la llamada para obtener las subastas desde el repositorio.
     * Esto se ejecuta en una coroutine en el viewModelScope, lo que asegura que
     * la operación se cancela automáticamente si el ViewModel se destruye.
     * @param query El término de búsqueda a usar. Si es nulo, usa el valor actual de _searchQuery.
     */
    fun fetchAuctions(query: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true // Indica que la carga ha comenzado
            _errorMessage.value = null // Limpia cualquier mensaje de error anterior
            val currentQuery = query ?: _searchQuery.value // Usa el query pasado o el actual
            val fetchedAuctions = repository.getAuctions(currentQuery)
            if (fetchedAuctions != null) {
                _auctions.value = fetchedAuctions // Actualiza la lista de subastas
            } else {
                _errorMessage.value = "Error al cargar subastas. Inténtalo de nuevo."
            }
            _isLoading.value = false // Indica que la carga ha terminado
        }
    }

    /**
     * Función que se llama cuando el usuario inicia una búsqueda explícita.
     * Llama a fetchAuctions con el query actual.
     */
    fun performSearch() {
        fetchAuctions(_searchQuery.value)
    }

    /**
     * Función para simular la actualización de una subasta tras una puja (solo para UI de ejemplo).
     * En una app real, esto podría venir de una actualización de la API o un WebSocket.
     */
    fun updateAuctionInList(updatedAuction: Auction) {
        _auctions.value = _auctions.value.map { auction ->
            if (auction.id == updatedAuction.id) {
                updatedAuction
            } else {
                auction
            }
        }
    }
}
