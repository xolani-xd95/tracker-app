package co.za.xdcodes.trackerconnect.feature.details

import co.za.xdcodes.trackerconnect.core.model.Shipment

sealed interface ShipmentDetailsUiState {
    data object Loading : ShipmentDetailsUiState
    data class Success(val shipment: Shipment) : ShipmentDetailsUiState
    data class Error(val message: String) : ShipmentDetailsUiState
}