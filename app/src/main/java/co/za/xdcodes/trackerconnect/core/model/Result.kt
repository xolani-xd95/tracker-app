package co.za.xdcodes.trackerconnect.core.model

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: String? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}
