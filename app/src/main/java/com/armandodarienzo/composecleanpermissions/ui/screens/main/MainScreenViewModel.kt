package com.armandodarienzo.composecleanpermissions.ui.screens.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.armandodarienzo.composecleanpermissions.domain.base.Result
import com.armandodarienzo.composecleanpermissions.domain.bluetooth.GetPairedDevicesUseCase
import com.armandodarienzo.composecleanpermissions.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getPairedDevices: GetPairedDevicesUseCase,
) :  BaseViewModel<MainScreenViewModel.MainScreenAction, MainScreenReducer.MainScreenState,
        MainScreenReducer.Event, MainScreenReducer.Effect> (
    initialState = MainScreenReducer.MainScreenState.initial(),
    reducer = MainScreenReducer()
) {


    sealed class MainScreenAction : Action {
        // --- Complex Actions (Require logic in the Processor) ---
        data object ConnectClicked : MainScreenAction()

        // --- Simple Actions (Directly map to an Event via the interface) ---
        data object DismissDialog : MainScreenAction()
    }

    override fun processAction(action: MainScreenAction) {
        when (action) {
            MainScreenAction.ConnectClicked -> onConnectClick()
            MainScreenAction.DismissDialog -> sendEvent(MainScreenReducer.Event.DismissDialog)
        }
    }

    private fun onConnectClick() {
        viewModelScope.launch {

            when (val op = getPairedDevices(Unit)) {
                is Result.BusinessRuleError -> when(val error = op.error) {
                    GetPairedDevicesUseCase.GetPairedDevicesError.NoDevicesFound ->
                        sendEvent(MainScreenReducer.Event.ShowDialogError("No devices found"))

                    GetPairedDevicesUseCase.GetPairedDevicesError.BluetoothShutDown ->
                        sendEvent(MainScreenReducer.Event.ShowDialogError("Bluetooth is off"))

                    is GetPairedDevicesUseCase.GetPairedDevicesError.MissingPermissions -> {
                        sendEffect(
                            MainScreenReducer.Command.ExecuteWithPermissions(
                                error.permissions,
                                MainScreenAction.ConnectClicked,
                                rationaleMessage = "These permissions are needed to search for " +
                                        "and connect to OBD devices."
                            )
                        )
                    }
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