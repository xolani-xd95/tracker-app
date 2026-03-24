package co.za.xdcodes.trackerconnect.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val error: ErrorDetail
)

@Serializable
data class ErrorDetail(
    val code: String,
    val message: String
)
