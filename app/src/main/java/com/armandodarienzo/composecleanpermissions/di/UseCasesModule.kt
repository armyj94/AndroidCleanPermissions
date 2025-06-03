package com.armandodarienzo.composecleanpermissions.di

import com.armandodarienzo.composecleanpermissions.domain.bluetooth.BluetoothRepository
import com.armandodarienzo.composecleanpermissions.domain.bluetooth.GetPairedDevicesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideGetPairedDevicesUseCase(
        bluetoothRepository: BluetoothRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): GetPairedDevicesUseCase {
        return GetPairedDevicesUseCase(
            bluetoothRepository = bluetoothRepository,
            dispatcher = ioDispatcher
        )
    }

}