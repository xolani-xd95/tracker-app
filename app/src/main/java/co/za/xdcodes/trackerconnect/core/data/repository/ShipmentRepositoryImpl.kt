package co.za.xdcodes.trackerconnect.core.data.repository

import co.za.xdcodes.trackerconnect.core.data.local.dao.ShipmentDao
import co.za.xdcodes.trackerconnect.core.data.local.entity.toDomain
import co.za.xdcodes.trackerconnect.core.data.local.entity.toEntity
import co.za.xdcodes.trackerconnect.core.data.source.ShipmentDataSource
import co.za.xdcodes.trackerconnect.core.domain.repository.ShipmentRepository
import co.za.xdcodes.trackerconnect.core.model.Result
import co.za.xdcodes.trackerconnect.core.model.Shipment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShipmentRepositoryImpl @Inject constructor(
    private val shipmentDao: ShipmentDao,
    private val dataSource: ShipmentDataSource
) : ShipmentRepository {

    override fun getShipments(): Flow<Result<List<Shipment>>> = flow {
        try {
            shipmentDao.getAllShipments()
                .map { entities -> entities.map { it.toDomain() } }
                .collect { shipments ->
                    emit(Result.Success(shipments))
                }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Failed to load shipments"))
        }
    }

    override suspend fun getShipmentDetails(id: String): Result<Shipment> {
        return try {
            val shipment = dataSource.fetchShipmentDetails(id)
            Result.Success(shipment)
        } catch (e: Exception) {
            try {
                val entity = shipmentDao.getShipmentById(id)
                if (entity != null) {
                    Result.Success(entity.toDomain())
                } else {
                    Result.Error("Shipment not found", "not_found")
                }
            } catch (dbError: Exception) {
                Result.Error(e.message ?: "Failed to load shipment details")
            }
        }
    }

    override suspend fun addTracking(
        trackingNumber: String,
        carrier: String?,
        title: String?
    ): Result<Shipment> {
        return try {
            val existing = shipmentDao.getShipmentByTrackingNumber(trackingNumber)
            if (existing != null) {
                return Result.Error("Tracking number already exists", "duplicate")
            }

            val shipment = dataSource.findShipmentByTrackingNumber(trackingNumber)
                ?: return Result.Error("Tracking number not found", "not_found")

            val updatedShipment = shipment.copy(
                carrier = carrier ?: shipment.carrier,
                title = title ?: shipment.title
            )
            shipmentDao.insertShipment(updatedShipment.toEntity())

            Result.Success(updatedShipment)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to add tracking number")
        }
    }

    override suspend fun refreshShipments(): Result<Unit> {
        return try {
            val localShipments = shipmentDao.getAllShipmentsSync()

            if (localShipments.isEmpty()) {
                return Result.Success(Unit)
            }

            val response = dataSource.fetchShipments()

            localShipments.forEach { localEntity ->
                val updatedShipment = response.items.find {
                    it.trackingNumber == localEntity.trackingNumber
                }

                if (updatedShipment != null) {
                    val merged = updatedShipment.copy(
                        carrier = localEntity.carrier,
                        title = localEntity.title,
                        isFavorite = localEntity.isFavorite
                    )
                    shipmentDao.insertShipment(merged.toEntity())
                }
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to refresh shipments")
        }
    }

    override suspend fun toggleFavorite(id: String, isFavorite: Boolean): Result<Unit> {
        return try {
            shipmentDao.updateFavoriteStatus(id, isFavorite)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update favorite status")
        }
    }
}
