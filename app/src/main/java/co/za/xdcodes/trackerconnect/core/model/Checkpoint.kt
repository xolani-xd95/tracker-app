package co.za.xdcodes.trackerconnect.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Checkpoint(
    val time: String,
    val location: String,
    val status: String,
    val message: String
)
