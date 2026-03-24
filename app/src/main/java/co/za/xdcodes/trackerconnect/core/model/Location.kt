package co.za.xdcodes.trackerconnect.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val city: String,
    val country: String
) {
    fun getDisplayName(): String = "$city, $country"
}
