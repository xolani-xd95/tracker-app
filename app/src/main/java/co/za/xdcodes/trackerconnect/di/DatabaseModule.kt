package co.za.xdcodes.trackerconnect.di

import android.content.Context
import co.za.xdcodes.trackerconnect.core.data.local.TrackerDatabase
import co.za.xdcodes.trackerconnect.core.data.local.dao.ShipmentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTrackerDatabase(
        @ApplicationContext context: Context
    ): TrackerDatabase {
        return TrackerDatabase.getInstance(context)
    }

    @Provides
    fun provideShipmentDao(database: TrackerDatabase): ShipmentDao {
        return database.shipmentDao()
    }
}
