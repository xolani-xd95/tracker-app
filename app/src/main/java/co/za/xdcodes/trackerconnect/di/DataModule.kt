package co.za.xdcodes.trackerconnect.di

import android.content.Context
import co.za.xdcodes.trackerconnect.core.data.source.ShipmentDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideShipmentDataSource(
        @ApplicationContext context: Context
    ): ShipmentDataSource {
        return ShipmentDataSource(context)
    }
}
