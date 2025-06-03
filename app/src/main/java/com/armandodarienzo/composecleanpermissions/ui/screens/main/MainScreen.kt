package com.armandodarienzo.composecleanpermissions.ui.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.armandodarienzo.composecleanpermissions.ui.base.rememberFlowWithLifecycle
import com.armandodarienzo.composecleanpermissions.ui.permissions.PermissionRationaleDialog
import com.armandodarienzo.composecleanpermissions.ui.permissions.PermissionsPermanentlyDeniedDialog
import com.armandodarienzo.composecleanpermissions.ui.permissions.PermissionReducer

@Composable
fun MainScreen(
    modifier: Modifier,
    viewModel: MainScreenViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    val effectFlow = rememberFlowWithLifecycle(viewModel.effect)

    MainScreenEffectsHandler(
        effectFlow = effectFlow,
        scope = viewModel.viewModelScope,
        sendEvent = { event -> viewModel.sendEvent(event) },
        sendEventForEffect = { event -> viewModel.sendEventForEffect(event) }
    )

    Content(
        modifier = modifier,
        state = state.value,
        sendEvent = { event -> viewModel.sendEvent(event) },
        sendEventForEffect = { event -> viewModel.sendEventForEffect(event) },
        connectClick = { viewModel.onConnectClick() }
    )

}

@SuppressLint("MissingPermission")
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    state: MainScreenReducer.MainScreenState,
    sendEvent: (MainScreenReducer.Event) -> Unit = {},
    sendEventForEffect: (MainScreenReducer.Event) -> Unit = {},
    connectClick: () -> Unit = {},
    ) {

    with(state) {

        PermissionRationaleDialog(
            showDialog = permissionState.showRationaleDialog,
            message = "These permissions are needed for a connection with the device",
            onAskAgainClick = {
                sendEventForEffect(
                    MainScreenReducer.Event.Permission(
                        PermissionReducer.PermissionEvent.HideRationaleDialog(
                            true,
                            permissionState.currentPermissionsRequested
                        )
                    )
                )
            },
            onDismissClick = {
                sendEvent(
                    MainScreenReducer.Event.Permission(
                        PermissionReducer.PermissionEvent.HideRationaleDialog(
                            false,
                            permissionState.currentPermissionsRequested
                        )
                    )
                )
            }
        )

        PermissionsPermanentlyDeniedDialog(
            showDialog = permissionState.showDeniedDialog,
            permanentlyDeniedPermissions = permissionState.currentPermissionsRequested,
            onGoToSettingsClick = {
                sendEventForEffect(
                    MainScreenReducer.Event.Permission(
                        PermissionReducer.PermissionEvent.HideDeniedDialog(true)
                    )
                )
            },
            onDismissClick = {
                sendEvent(
                    MainScreenReducer.Event.Permission(
                        PermissionReducer.PermissionEvent.HideDeniedDialog(
                            false
                        )
                    )
                )
            }
        )

        if (showDialog) {
            AlertDialog(
                modifier = Modifier
                    .height(600.dp)
                    .width(340.dp),
                onDismissRequest = {
                    sendEvent(MainScreenReducer.Event.DismissDialog)
                },
                title = { Text("Paired Bluetooth Devices") },
                text = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                            if (pairedDevices.isEmpty()) {
                                Text(dialogErrorMessage)
                            } else {
                                LazyColumn {
                                    items(pairedDevices.size) { index ->
                                        val device = pairedDevices[index]
                                        Text(
                                            text = device.name,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                                .clickable {
                                                    //CONNECTION LOGIC
                                                }
                                        )
                                    }
                                }
                            }

                    }

                },
                confirmButton = {
                    TextButton(onClick = {
                        // Direct call to sendEvent using the original logic
                        sendEvent(MainScreenReducer.Event.DismissDialog)
                    }) {
                        Text("Close")
                    }
                }
            )
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    connectClick()
                },
                enabled = true,
            ) {
                Text(
                    text =
                        "Connect"
                )
            }
        }

    }

}