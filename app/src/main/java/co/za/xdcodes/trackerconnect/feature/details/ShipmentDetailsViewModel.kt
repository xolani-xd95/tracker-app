package co.za.xdcodes.trackerconnect.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.za.xdcodes.trackerconnect.core.domain.repository.ShipmentRepository
import co.za.xdcodes.trackerconnect.core.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShipmentDetailsViewModel @Inject constructor(
    private val repository: ShipmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val shipmentId: String = checkNotNull(savedStateHandle["shipmentId"])

    private val _uiState = MutableStateFlow<ShipmentDetailsUiState>(ShipmentDetailsUiState.Loading)
    val uiState: StateFlow<ShipmentDetailsUiState> = _uiState.asStateFlow()

    init {
        loadShipmentDetails()
    }

    private fun loadShipmentDetails() {
        viewModelScope.launch {
            _uiState.value = ShipmentDetailsUiState.Loading

            when (val result = repository.getShipmentDetails(shipmentId)) {
                is Result.Success -> _uiState.value = ShipmentDetailsUiState.Success(result.data)
                is Result.Error -> _uiState.value = ShipmentDetailsUiState.Error(result.message)
                is Result.Loading -> _uiState.value = ShipmentDetailsUiState.Loading
            }
        }
    }

    fun retry() {
        loadShipmentDetails()
    }
}

