package co.za.xdcodes.trackerconnect.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.za.xdcodes.trackerconnect.core.data.local.entity.ShipmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShipmentDao {

    @Query("SELECT * FROM shipments ORDER BY last_update DESC")
    fun getAllShipments(): Flow<List<ShipmentEntity>>

    @Query("SELECT * FROM shipments ORDER BY last_update DESC")
    suspend fun getAllShipmentsSync(): List<ShipmentEntity>

    @Query("SELECT * FROM shipments WHERE id = :id")
    suspend fun getShipmentById(id: String): ShipmentEntity?

    @Query("SELECT * FROM shipments WHERE tracking_number = :trackingNumber")
    suspend fun getShipmentByTrackingNumber(trackingNumber: String): ShipmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShipment(shipment: ShipmentEntity)

    @Query("UPDATE shipments SET is_favorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)
}
