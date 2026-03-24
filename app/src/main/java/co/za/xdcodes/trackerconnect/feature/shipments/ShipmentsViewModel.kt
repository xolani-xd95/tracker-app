package co.za.xdcodes.trackerconnect.feature.shipments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.za.xdcodes.trackerconnect.core.domain.repository.ShipmentRepository
import co.za.xdcodes.trackerconnect.core.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShipmentsViewModel @Inject constructor(
    private val repository: ShipmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShipmentsUiState())
    val uiState: StateFlow<ShipmentsUiState> = _uiState.asStateFlow()

    init {
        loadShipmentsFromDb()
    }

    /**
     * Loads ALL shipments from DB and stores in memory
     * All filtering (search, favorites) happens in the UI state
     */
    private fun loadShipmentsFromDb() {
        viewModelScope.launch {
            repository.getShipments().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                allShipments = result.data,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                    is Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                }
            }
        }
    }

    fun refreshShipments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            when (val result = repository.refreshShipments()) {
                is Result.Success -> {
                    _uiState.update { it.copy(isRefreshing = false) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            error = result.message
                        )
                    }
                }
                else -> {}
            }
        }
    }

    fun showAddTrackingDialog() {
        _uiState.update { it.copy(showAddDialog = true, addTrackingError = null) }
    }

    fun hideAddTrackingDialog() {
        _uiState.update {
            it.copy(
                showAddDialog = false,
                addTrackingError = null,
                trackingNumber = "",
                carrier = "",
                title = ""
            )
        }
    }

    fun updateTrackingNumber(value: String) {
        _uiState.update { it.copy(trackingNumber = value, addTrackingError = null) }
    }

    fun updateCarrier(value: String) {
        _uiState.update { it.copy(carrier = value) }
    }

    fun updateTitle(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun addTracking() {
        val currentState = _uiState.value
        if (currentState.trackingNumber.isBlank()) {
            _uiState.update { it.copy(addTrackingError = "Tracking number is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isAddingTracking = true, addTrackingError = null) }

            val result = repository.addTracking(
                trackingNumber = currentState.trackingNumber.trim(),
                carrier = currentState.carrier.trim().ifBlank { null },
                title = currentState.title.trim().ifBlank { null }
            )

            when (result) {
                is Result.Success -> {
                    hideAddTrackingDialog()
                    _uiState.update { it.copy(isAddingTracking = false) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isAddingTracking = false,
                            addTrackingError = result.message
                        )
                    }
                }
                else -> {}
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        // No DB call - filtering happens in UI state
    }

    fun toggleFavorite(shipmentId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(shipmentId, isFavorite)
        }
    }

    fun toggleShowFavoritesOnly() {
        _uiState.update { it.copy(showFavoritesOnly = !it.showFavoritesOnly) }
        // Filtering happens in UI state computed property
    }
}