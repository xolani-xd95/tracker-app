package co.za.xdcodes.trackerconnect.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ShipmentStatus(val displayName: String) {
    @SerialName("created")
    CREATED("Created"),

    @SerialName("picked_up")
    PICKED_UP("Picked Up"),

    @SerialName("in_transit")
    IN_TRANSIT("In Transit"),

    @SerialName("out_for_delivery")
    OUT_FOR_DELIVERY("Out for Delivery"),

    @SerialName("delivered")
    DELIVERED("Delivered"),

    @SerialName("exception")
    EXCEPTION("Exception"),

    @SerialName("unknown")
    UNKNOWN("Unknown")
}