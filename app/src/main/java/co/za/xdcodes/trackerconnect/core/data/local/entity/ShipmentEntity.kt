package co.za.xdcodes.trackerconnect.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import co.za.xdcodes.trackerconnect.core.model.Location
import co.za.xdcodes.trackerconnect.core.model.Shipment
import co.za.xdcodes.trackerconnect.core.model.ShipmentStatus

@Entity(tableName = "shipments")
data class ShipmentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "tracking_number")
    val trackingNumber: String,

    @ColumnInfo(name = "carrier")
    val carrier: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "last_update")
    val lastUpdate: String,

    @ColumnInfo(name = "eta")
    val eta: String? = null,

    @ColumnInfo(name = "origin_city")
    val originCity: String? = null,

    @ColumnInfo(name = "origin_country")
    val originCountry: String? = null,

    @ColumnInfo(name = "destination_city")
    val destinationCity: String? = null,

    @ColumnInfo(name = "destination_country")
    val destinationCountry: String? = null,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false
)

fun Shipment.toEntity(): ShipmentEntity {
    return ShipmentEntity(
        id = id,
        trackingNumber = trackingNumber,
        carrier = carrier,
        title = title,
        status = status.name.lowercase(),
        lastUpdate = lastUpdate,
        eta = eta,
        originCity = origin?.city,
        originCountry = origin?.country,
        destinationCity = destination?.city,
        destinationCountry = destination?.country,
        isFavorite = this.isFavorite
    )
}

fun ShipmentEntity.toDomain(): Shipment {
    return Shipment(
        id = id,
        trackingNumber = trackingNumber,
        carrier = carrier,
        title = title,
        status = try {
            ShipmentStatus.valueOf(status.uppercase())
        } catch (e: IllegalArgumentException) {
            ShipmentStatus.UNKNOWN
        },
        lastUpdate = lastUpdate,
        eta = eta,
        origin = if (originCity != null && originCountry != null) {
            Location(originCity, originCountry)
        } else null,
        destination = if (destinationCity != null && destinationCountry != null) {
            Location(destinationCity, destinationCountry)
        } else null,
        checkpoints = null,
        isFavorite = isFavorite
    )
}
