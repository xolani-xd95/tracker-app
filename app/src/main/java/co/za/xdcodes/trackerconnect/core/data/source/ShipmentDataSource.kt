package co.za.xdcodes.trackerconnect.core.data.source

import android.content.Context
import co.za.xdcodes.trackerconnect.core.model.Shipment
import co.za.xdcodes.trackerconnect.core.model.ShipmentsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.IOException

class ShipmentDataSource(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun fetchShipments(): ShipmentsResponse = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("shipments.json")
                .bufferedReader()
                .use { it.readText() }

            json.decodeFromString<ShipmentsResponse>(jsonString)
        } catch (e: IOException) {
            throw Exception("Failed to load shipments: ${e.message}")
        }
    }

    suspend fun fetchShipmentDetails(id: String): Shipment = withContext(Dispatchers.IO) {
        try {
            val fileName = "shipment_$id.json"
            val jsonString = context.assets.open(fileName)
                .bufferedReader()
                .use { it.readText() }

            json.decodeFromString<Shipment>(jsonString)
        } catch (e: IOException) {
            throw Exception("Shipment not found")
        }
    }

    suspend fun findShipmentByTrackingNumber(trackingNumber: String): Shipment? =
        withContext(Dispatchers.IO) {
            try {
                val response = fetchShipments()
                response.items.find { it.trackingNumber == trackingNumber }
            } catch (e: Exception) {
                null
            }
        }
}
