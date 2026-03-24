package co.za.xdcodes.trackerconnect.core.domain.repository

import co.za.xdcodes.trackerconnect.core.model.Result
import co.za.xdcodes.trackerconnect.core.model.Shipment
import kotlinx.coroutines.flow.Flow

interface ShipmentRepository {

    fun getShipments(): Flow<Result<List<Shipment>>>

    suspend fun getShipmentDetails(id: String): Result<Shipment>

    suspend fun addTracking(
        trackingNumber: String,
        carrier: String? = null,
        title: String? = null
    ): Result<Shipment>

    suspend fun refreshShipments(): Result<Unit>

    suspend fun toggleFavorite(id: String, isFavorite: Boolean): Result<Unit>
}
