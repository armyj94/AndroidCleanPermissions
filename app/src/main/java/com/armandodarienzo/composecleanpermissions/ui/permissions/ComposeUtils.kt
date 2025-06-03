package com.armandodarienzo.composecleanpermissions.ui.permissions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue

@Composable
fun rememberPermissionLauncherWithCallbacks():
            (permissions: List<String>, onGranted: () -> Unit, onDenied: () -> Unit) -> Unit {

    // State to temporarily hold the callbacks for the *currently active* launch.
    // It's nullable to indicate no launch is pending or result handled.
    var currentLaunchCallbacks by remember {
        mutableStateOf<Pair<(() -> Unit)?, (() -> Unit)?>?>(null)
    }

    // Use rememberUpdatedState to ensure the onResult lambda always refers
    // to the latest callbacks stored in currentLaunchCallbacks state.
    val latestOnGranted by rememberUpdatedState(currentLaunchCallbacks?.first)
    val latestOnDenied by rememberUpdatedState(currentLaunchCallbacks?.second)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        val allGranted = map.values.all { it } // Check if ALL permissions were granted

        if (allGranted) {
            latestOnGranted?.invoke()
        } else {
            latestOnDenied?.invoke()
        }

        // Clear the state after handling the result to prevent accidental re-invocation
        // if recomposition happens later while the state still holds old callbacks.
        currentLaunchCallbacks = null
    }

    // Return a lambda function that the caller will use to initiate the request.
    // This lambda captures the 'launcher' and the state setter 'currentLaunchCallbacks'.
    return { permissions: List<String>, onGranted: () -> Unit, onDenied: () -> Unit ->
        // Store the specific callbacks provided for *this* launch in our state
        currentLaunchCallbacks = Pair(onGranted, onDenied)
        // Launch the permission request using the underlying launcher
        launcher.launch(permissions.toTypedArray())
    }
}