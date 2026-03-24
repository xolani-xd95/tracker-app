package co.za.xdcodes.trackerconnect.di

import co.za.xdcodes.trackerconnect.core.data.repository.ShipmentRepositoryImpl
import co.za.xdcodes.trackerconnect.core.domain.repository.ShipmentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindShipmentRepository(
        impl: ShipmentRepositoryImpl
    ): ShipmentRepository
}
