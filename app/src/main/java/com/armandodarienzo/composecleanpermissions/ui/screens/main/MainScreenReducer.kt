package com.armandodarienzo.composecleanpermissions.ui.screens.main

import androidx.compose.runtime.Immutable
import com.armandodarienzo.composecleanpermissions.domain.base.PermissionRequest
import com.armandodarienzo.composecleanpermissions.domain.bluetooth.PairedDevice
import com.armandodarienzo.composecleanpermissions.ui.base.Reducer

class MainScreenReducer:
    Reducer<MainScreenReducer.MainScreenState, MainScreenReducer.Event, MainScreenReducer.Effect> {

    @Immutable
    data class MainScreenState(
        val showDialog: Boolean,
        val pairedDevices: List<PairedDevice>,
        val dialogErrorMessage: String,
    ) : Reducer.ViewState {

        companion object{
            fun initial() : MainScreenState {
                return MainScreenState(
                    showDialog = false,
                    pairedDevices = emptyList(),
                    dialogErrorMessage = "",
                )
            }
        }
    }

    @Immutable
    sealed class Event() : Reducer.ViewEvent {
        data object ShowDialog : Event()
        data object DismissDialog : Event()
        data class UpdatePairedDevices(val pairedDevices: List<PairedDevice>): Event()
        data class ShowDialogError(val errorMessage: String) : Event()

    }

    @Immutable
    sealed class Effect : Reducer.SideEffect

    sealed class ViewEffect : Effect() {
        data class ShowSuccessSnackBar(val successMessage: String) : ViewEffect()
        data class ShowErrorSnackbar(val errorMessage: String) : ViewEffect()
    }

    data class MainScreenPermissionRequest(
        override val permissions: List<String>,
        override val actionToExecute: MainScreenViewModel.MainScreenAction,
        override val rationaleMessage: String?
    ) : Effect(), PermissionRequest<MainScreenViewModel.MainScreenAction>

    override fun reduce(
        previousState: MainScreenState,
        event: Event
    ): Pair<MainScreenState, Effect?> {
        return when(event) {
            is Event.ShowDialog -> {
                previousState.copy(
                    showDialog = true
                ) to null
            }

            is Event.DismissDialog -> {
                previousState.copy(
                    showDialog = false
                ) to null
            }

            is Event.UpdatePairedDevices -> {
                previousState.copy(
                    showDialog = true,
                    pairedDevices = event.pairedDevices
                ) to null
            }

            is Event.ShowDialogError -> {
                previousState.copy(
                    showDialog = true,
                    dialogErrorMessage = event.errorMessage
                ) to null
            }
        }
    }

}