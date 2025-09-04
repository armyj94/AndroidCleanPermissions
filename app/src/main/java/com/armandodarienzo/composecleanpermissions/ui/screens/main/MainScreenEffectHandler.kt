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
) {
    val context = LocalContext.current

    LaunchedEffect(effectFlow) {
        effectFlow
            .filterIsInstance<MainScreenReducer.ViewEffect>()
            .collect { effect ->
                when (effect) {

                    is MainScreenReducer.ViewEffect.ShowErrorSnackbar -> {
                        scope.launch {
                            Toast.makeText(context, effect.errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }

                    is MainScreenReducer.ViewEffect.ShowSuccessSnackBar -> {
                        scope.launch {
                            Toast.makeText(context, effect.successMessage, Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
    }
}