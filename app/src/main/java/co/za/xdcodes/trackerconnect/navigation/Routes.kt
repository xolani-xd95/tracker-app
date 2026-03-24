package co.za.xdcodes.trackerconnect.navigation

import kotlinx.serialization.Serializable

sealed class Routes {
    @Serializable
    object Shipments

    @Serializable
    data class ShipmentDetails(val shipmentId: String)
}