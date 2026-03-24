package co.za.xdcodes.trackerconnect.feature.shipments

import co.za.xdcodes.trackerconnect.core.model.Shipment

/**
 * UI state for shipments screen
 */
data class ShipmentsUiState(
    val allShipments: List<Shipment> = emptyList(), // All loaded shipments from DB
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val trackingNumber: String = "",
    val carrier: String = "",
    val title: String = "",
    val isAddingTracking: Boolean = false,
    val addTrackingError: String? = null,
    val showFavoritesOnly: Boolean = false
) {
    val shipments: List<Shipment>
        get() {
            var filtered = allShipments

            // Filter by favorites if enabled
            if (showFavoritesOnly) {
                filtered = filtered.filter { it.isFavorite }
            }

            // Filter by search query
            if (searchQuery.isNotBlank()) {
                filtered = filtered.filter {
                    it.trackingNumber.contains(searchQuery, ignoreCase = true) ||
                    it.title.contains(searchQuery, ignoreCase = true) ||
                    it.carrier.contains(searchQuery, ignoreCase = true)
                }
            }

            return filtered
        }
}
