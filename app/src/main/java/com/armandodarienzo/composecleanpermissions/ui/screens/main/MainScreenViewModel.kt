package com.armandodarienzo.composecleanpermissions.ui.screens.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.armandodarienzo.composecleanpermissions.domain.base.Result
import com.armandodarienzo.composecleanpermissions.domain.bluetooth.GetPairedDevicesUseCase
import com.armandodarienzo.composecleanpermissions.ui.base.BaseViewModel
import com.armandodarienzo.composecleanpermissions.ui.permissions.PermissionReducer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getPairedDevices: GetPairedDevicesUseCase,
) : BaseViewModel<MainScreenReducer.MainScreenState, MainScreenReducer.Event, MainScreenReducer.Effect>(
    initialState = MainScreenReducer.MainScreenState.initial(),
    reducer = MainScreenReducer()
) {

    fun onConnectClick() {
        viewModelScope.launch {

            when (val op = getPairedDevices(Unit)) {
                is Result.BusinessRuleError -> when(op.error) {
                    GetPairedDevicesUseCase.GetPairedDevicesError.NoDevicesFound ->
                        sendEvent(MainScreenReducer.Event.ShowDialogError("No devices found"))

                    GetPairedDevicesUseCase.GetPairedDevicesError.BluetoothShutDown ->
                        sendEvent(MainScreenReducer.Event.ShowDialogError("Bluetooth is off"))

                    is GetPairedDevicesUseCase.GetPairedDevicesError.MissingPermissions ->
                        sendEventForEffect(
                            MainScreenReducer.Event.Permission(
                                PermissionReducer.PermissionEvent.StartPermissionRequest(
                                    op.error.permissions,
                                    ::onConnectClick
                                )
                            )
                        )
                }
                is Result.Error -> {
                    //Properly log the error
                    Log.e("MainScreenViewModel", op.error.originalException.stackTraceToString())
                }
                Result.Loading -> Unit

                is Result.Success -> when (op.successData) {
                    is GetPairedDevicesUseCase.GetPairedDevicesSuccess.DevicesData ->
                        sendEvent(MainScreenReducer.Event.UpdatePairedDevices(op.successData.devices))
                }
            }
        }
    }

}