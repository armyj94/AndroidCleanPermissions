package com.armandodarienzo.composecleanpermissions.ui.screens.main

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

@Composable
fun MainScreenEffectsHandler(
    effectFlow: Flow<MainScreenReducer.Effect>,
    scope: CoroutineScope,
    sendEvent: (event: MainScreenReducer.Event) -> Unit,
    sendEventForEffect: (event: MainScreenReducer.Event) -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(effectFlow) {
        effectFlow
            .filterIsInstance<MainScreenReducer.UiEffect>()
            .collect { effect ->
                when (effect) {

                    is MainScreenReducer.UiEffect.ShowErrorSnackbar -> {
                        scope.launch {
                            Toast.makeText(context, effect.errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }

                    is MainScreenReducer.UiEffect.ShowSuccessSnackBar -> {
                        scope.launch {
                            Toast.makeText(context, effect.successMessage, Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
    }
}