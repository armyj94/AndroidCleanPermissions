package com.armandodarienzo.composecleanpermissions.di

import com.armandodarienzo.composecleanpermissions.domain.bluetooth.BluetoothRepository
import com.armandodarienzo.composecleanpermissions.domain.bluetooth.GetPairedDevicesUseCase
import com.armandodarienzo.composecleanpermissions.ui.base.EffectDelegate
import com.armandodarienzo.composecleanpermissions.ui.base.StandardEffectDelegate
import com.armandodarienzo.composecleanpermissions.ui.screens.main.MainScreenReducer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(ViewModelComponent::class)
object MainModule {

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

    @Provides
    @ViewModelScoped
    fun provideEffectDelegate() : StandardEffectDelegate<MainScreenReducer.Effect> {
        return StandardEffectDelegate()
    }

}