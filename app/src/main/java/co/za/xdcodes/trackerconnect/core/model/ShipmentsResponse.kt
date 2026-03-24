package co.za.xdcodes.trackerconnect.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ShipmentsResponse(
    val items: List<Shipment>,
    val total: Int
)
