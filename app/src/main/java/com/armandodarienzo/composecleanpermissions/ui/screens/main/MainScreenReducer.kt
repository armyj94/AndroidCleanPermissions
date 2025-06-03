package com.armandodarienzo.composecleanpermissions.ui.screens.main

import androidx.compose.runtime.Immutable
import com.armandodarienzo.composecleanpermissions.domain.bluetooth.PairedDevice
import com.armandodarienzo.composecleanpermissions.ui.permissions.PermissionReducer
import com.armandodarienzo.composecleanpermissions.ui.base.Reducer

class MainScreenReducer:
    Reducer<MainScreenReducer.MainScreenState, MainScreenReducer.Event, MainScreenReducer.Effect> {

    @Immutable
    data class MainScreenState(
        val showDialog: Boolean,
        val pairedDevices: List<PairedDevice>,
        val dialogErrorMessage: String,
        val permissionState: PermissionReducer.PermissionViewState
    ) : Reducer.ViewState {

        companion object{
            fun initial() : MainScreenState {
                return MainScreenState(
                    showDialog = false,
                    pairedDevices = emptyList(),
                    dialogErrorMessage = "",
                    permissionState = PermissionReducer.PermissionViewState.initial()
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
        data class Permission(val event: PermissionReducer.PermissionEvent) : Event()
    }

    @Immutable
    sealed class Effect : Reducer.ViewEffect {
        data class Permission(val effect: PermissionReducer.PermissionEffect) : Effect()
    }

    private val permissionReducer = PermissionReducer()

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

            is Event.Permission -> {

                val (updatedPermissionState, permissionEffect) = permissionReducer.reduce(
                    previousState.permissionState,
                    event.event
                )

                val mainEffect = permissionEffect?.let { Effect.Permission(it) }
                previousState.copy(permissionState = updatedPermissionState) to mainEffect
            }
        }
    }

}