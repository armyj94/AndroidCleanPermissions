package com.armandodarienzo.composecleanpermissions.ui.permissions

import androidx.compose.runtime.Immutable
import com.armandodarienzo.composecleanpermissions.ui.base.Reducer

class PermissionReducer :
    Reducer<PermissionReducer.PermissionViewState, PermissionReducer.PermissionEvent, PermissionReducer.PermissionEffect> {

    @Immutable
    data class PermissionViewState(
        val currentPermissionsRequested: List<String>,
        val showRationaleDialog: Boolean,
        val showDeniedDialog: Boolean,
        val actionWaitingForPermission: (() -> Unit)?
    ) : Reducer.ViewState {
        companion object {
            fun initial() = PermissionViewState(
                currentPermissionsRequested = emptyList(),
                showRationaleDialog = false,
                showDeniedDialog = false,
                actionWaitingForPermission = null
            )
        }
    }

    @Immutable
    sealed class PermissionEvent : Reducer.ViewEvent { // <-- Implement ViewEvent
        data class StartPermissionRequest(val permissions: List<String>, val actionWaitingForPermission: () -> Unit) : PermissionEvent()
        data class ShowRationaleDialog(val permissions: List<String>) : PermissionEvent()
        data class HideRationaleDialog(val askAgain: Boolean, val permissions: List<String>) : PermissionEvent()
        data class ShowDeniedDialog(val permissions: List<String>): PermissionEvent()
        data class HideDeniedDialog(val goToSettings: Boolean) : PermissionEvent()
        data object PermissionsGranted : PermissionEvent()
    }

    @Immutable
    sealed class PermissionEffect : Reducer.ViewEffect { // <-- Implement ViewEffect
        data class AskPermissions(val permissions: List<String>) : PermissionEffect()
        data object GoToPermissionSettings: PermissionEffect()
        data class RefireActionWaitingForPermission(val action: () -> Unit) : PermissionEffect()
    }

    override fun reduce(
        previousState: PermissionViewState,
        event: PermissionEvent
    ): Pair<PermissionViewState, PermissionEffect?> {
        return when(event) {
            is PermissionEvent.StartPermissionRequest -> {
                previousState.copy(
                    currentPermissionsRequested = event.permissions,
                    actionWaitingForPermission = event.actionWaitingForPermission
                ) to PermissionEffect.AskPermissions(event.permissions)
            }

            is PermissionEvent.ShowDeniedDialog -> {
                previousState.copy(
                    currentPermissionsRequested = event.permissions,
                    showDeniedDialog = true
                ) to null
            }

            is PermissionEvent.ShowRationaleDialog -> {
                previousState.copy(
                    currentPermissionsRequested = event.permissions,
                    showRationaleDialog = true
                ) to null
            }

            is PermissionEvent.HideDeniedDialog -> {
                val effect = if(event.goToSettings) {
                    PermissionEffect.GoToPermissionSettings
                } else {
                    null
                }

                previousState.copy(
                    currentPermissionsRequested = emptyList(), // Clear requested permissions after dialog
                    showDeniedDialog = false
                ) to effect
            }

            is PermissionEvent.HideRationaleDialog -> {
                val effect = if(event.askAgain) {
                    PermissionEffect.AskPermissions(event.permissions)
                } else {
                    null
                }

                previousState.copy(
                    currentPermissionsRequested = if (event.askAgain) event.permissions else emptyList(),
                    showRationaleDialog = false
                ) to effect
            }

            is PermissionEvent.PermissionsGranted -> {
                val action = previousState.actionWaitingForPermission
                val effect = action?.let {
                    PermissionEffect.RefireActionWaitingForPermission(it)
                }

                previousState.copy(
                    currentPermissionsRequested = emptyList(), // Clear requested permissions on grant
                    actionWaitingForPermission = null
                ) to effect
            }
        }
    }


}