package co.za.xdcodes.trackerconnect.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Shipment(
    val id: String,
    @SerialName("tracking_number")
    val trackingNumber: String,
    val carrier: String,
    val title: String,
    val status: ShipmentStatus,
    @SerialName("last_update")
    val lastUpdate: String,
    val eta: String? = null,
    val origin: Location? = null,
    val destination: Location? = null,
    val checkpoints: List<Checkpoint>? = null,
    val isFavorite: Boolean = false
)