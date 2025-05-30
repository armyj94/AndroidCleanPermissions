package com.armandodarienzo.composecleanpermissions.ui.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.armandodarienzo.composecleanpermissions.ui.base.findActivity
import com.armandodarienzo.composecleanpermissions.ui.base.goToPermissionSettings
import com.armandodarienzo.composecleanpermissions.ui.permissions.rememberPermissionLauncherWithCallbacks
import com.armandodarienzo.composecleanpermissions.ui.permissions.PermissionReducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@Composable
fun MainScreenEffectsHandler(
    effectFlow: Flow<MainScreenReducer.Effect>,
    scope: CoroutineScope,
    sendEvent: (event: MainScreenReducer.Event) -> Unit,
    sendEventForEffect: (event: MainScreenReducer.Event) -> Unit,
) {
    val launchPermissionRequest = rememberPermissionLauncherWithCallbacks()
    val context = LocalContext.current

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {

                // OTHER EFFECTS

                is MainScreenReducer.Effect.Permission -> {
                    when (effect.effect) {
                        is PermissionReducer.PermissionEffect.AskPermissions -> {
                            launchPermissionRequest(
                                effect.effect.permissions,
                                {
                                    sendEventForEffect(
                                        MainScreenReducer.Event.Permission(
                                            PermissionReducer.PermissionEvent.PermissionsGranted
                                        )
                                    )
                                },
                                {
                                    var shouldShowRationaleDialog = false
                                    val askAgainPermissions = mutableListOf<String>()
                                    effect.effect.permissions.forEach{ permission ->
                                        context.findActivity()?.let { activity ->
                                            val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
                                            if (shouldShowRationale) {
                                                askAgainPermissions.add(permission)
                                            }
                                            shouldShowRationaleDialog = shouldShowRationaleDialog || shouldShowRationale
                                        }
                                    }

                                    if (shouldShowRationaleDialog) {
                                        sendEvent(MainScreenReducer.Event.Permission(
                                            PermissionReducer.PermissionEvent.ShowRationaleDialog(askAgainPermissions)))
                                    } else {
                                        sendEvent(MainScreenReducer.Event.Permission(
                                            PermissionReducer.PermissionEvent.ShowDeniedDialog(effect.effect.permissions)))
                                    }
                                }
                            )
                        }

                        PermissionReducer.PermissionEffect.GoToPermissionSettings -> {
                            context.goToPermissionSettings()
                        }

                        is PermissionReducer.PermissionEffect.RefireActionWaitingForPermission -> {
                            effect.effect.action()
                        }
                    }
                }
            }
        }
    }
}