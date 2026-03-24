package co.za.xdcodes.trackerconnect.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import co.za.xdcodes.trackerconnect.core.data.local.dao.ShipmentDao
import co.za.xdcodes.trackerconnect.core.data.local.entity.ShipmentEntity

@Database(
    entities = [ShipmentEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TrackerDatabase : RoomDatabase() {

    abstract fun shipmentDao(): ShipmentDao

    companion object {
        @Volatile
        private var INSTANCE: TrackerDatabase? = null

        fun getInstance(context: Context): TrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackerDatabase::class.java,
                    "tracker_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
