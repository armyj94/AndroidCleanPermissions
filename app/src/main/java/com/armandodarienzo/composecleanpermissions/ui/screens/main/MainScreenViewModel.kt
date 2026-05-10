package com.armandodarienzo.composecleanpermissions.ui.screens.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armandodarienzo.composecleanpermissions.domain.base.Result
import com.armandodarienzo.composecleanpermissions.domain.bluetooth.GetPairedDevicesUseCase
import com.armandodarienzo.composecleanpermissions.ui.base.EffectDelegate
import com.armandodarienzo.composecleanpermissions.ui.base.StandardEffectDelegate
import com.armandodarienzo.composecleanpermissions.ui.base.MviProcessor
import com.armandodarienzo.composecleanpermissions.ui.base.MviStoreDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getPairedDevices: GetPairedDevicesUseCase,
    private val effectDelegate: StandardEffectDelegate<MainScreenReducer.Effect>
) : ViewModel(), MviProcessor<MainScreenReducer.MainScreenState, MainScreenViewModel.MainScreenAction, MainScreenReducer.Effect>,
EffectDelegate<MainScreenReducer.Effect> by effectDelegate{

    private val store = MviStoreDelegate(
        initialState = MainScreenReducer.MainScreenState.initial(),
        scope = viewModelScope,
        reducer = MainScreenReducer(),
        effectDelegate = effectDelegate,
        initialDataLoad = { }
    )
    override val state = store.state


    sealed class MainScreenAction : MviProcessor.MviAction {
        // --- Complex Actions (Require logic in the Processor) ---
        data object ConnectClicked : MainScreenAction()

        // --- Simple Actions (Directly map to an Event via the interface) ---
        data object DismissDialog : MainScreenAction()
    }

    override fun processAction(action: MainScreenAction) {
        when (action) {
            MainScreenAction.ConnectClicked -> onConnectClick()
            MainScreenAction.DismissDialog -> store.sendEvent(MainScreenReducer.Event.DismissDialog)
        }
    }

    private fun onConnectClick() {
        viewModelScope.launch {

            when (val op = getPairedDevices(Unit)) {
                is Result.BusinessRuleError -> when(val error = op.error) {
                    GetPairedDevicesUseCase.GetPairedDevicesError.NoDevicesFound ->
                        store.sendEvent(MainScreenReducer.Event.ShowDialogError("No devices found"))

                    GetPairedDevicesUseCase.GetPairedDevicesError.BluetoothShutDown ->
                        store.sendEvent(MainScreenReducer.Event.ShowDialogError("Bluetooth is off"))

                    is GetPairedDevicesUseCase.GetPairedDevicesError.MissingPermissions -> {
                        sendEffect(
                            MainScreenReducer.MainScreenPermissionRequest(
                                permissions = error.permissions,
                                actionToExecute = MainScreenAction.ConnectClicked,
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
                        store.sendEvent(MainScreenReducer.Event.UpdatePairedDevices(op.successData.devices))
                }
            }
        }
    }
}