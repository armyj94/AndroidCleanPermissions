package com.armandodarienzo.composecleanpermissions.ui.permissions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.armandodarienzo.composecleanpermissions.domain.base.PermissionRequest
import com.armandodarienzo.composecleanpermissions.ui.base.BaseViewModel
import com.armandodarienzo.composecleanpermissions.ui.base.Reducer
import com.armandodarienzo.composecleanpermissions.ui.base.findActivity
import com.armandodarienzo.composecleanpermissions.ui.base.goToPermissionSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun <A : BaseViewModel.Action> PermissionsManager(
    effectFlow: Flow<Reducer.SideEffect>,
    sendAction: (A) -> Unit
) {
    val context = LocalContext.current

    var showRationaleDialog by rememberSaveable { mutableStateOf(false) }
    var showDeniedDialog by rememberSaveable { mutableStateOf(false) }
    var permissionsToShow by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var actionToExecute by remember { mutableStateOf<A?>(null) }
    var rationaleMessageToShow by rememberSaveable { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        val activity = context.findActivity()
        if (permissionsResult.all { it.value }) {
            actionToExecute?.let(sendAction)
        } else {
            val permanentlyDenied = permissionsResult.filter { (permission, isGranted) ->
                !isGranted && activity?.let { !ActivityCompat.shouldShowRequestPermissionRationale(it, permission) } ?: false
            }.keys.toList()

            val needsRationale = permissionsResult.filter { (permission, isGranted) ->
                !isGranted && activity?.let { ActivityCompat.shouldShowRequestPermissionRationale(it, permission) } ?: true
            }.keys.toList()

            permissionsToShow = permanentlyDenied + needsRationale
            if (permanentlyDenied.isNotEmpty()) {
                showDeniedDialog = true
            } else if (needsRationale.isNotEmpty()) {
                showRationaleDialog = true
            }
        }
    }

    LaunchedEffect(effectFlow, launcher) {
        effectFlow
            .filterIsInstance<PermissionRequest<A>>()
            .collect { effect ->
                actionToExecute = effect.actionToExecute
                rationaleMessageToShow = effect.rationaleMessage
                launcher.launch(effect.permissions.toTypedArray())
            }
    }

    if (showRationaleDialog) {
        PermissionRationaleDialog(
            showDialog = true,
            message = rationaleMessageToShow ?: "These permissions are needed to continue with the operation",
            onAskAgainClick = {
                showRationaleDialog = false
                launcher.launch(permissionsToShow.toTypedArray())
            },
            onDismissClick = { showRationaleDialog = false }
        )
    }

    if (showDeniedDialog) {
        PermissionsPermanentlyDeniedDialog(
            showDialog = true,
            permanentlyDeniedPermissions = permissionsToShow,
            onGoToSettingsClick = {
                showDeniedDialog = false
                context.goToPermissionSettings()
            },
            onDismissClick = { showDeniedDialog = false }
        )
    }
}