package com.armandodarienzo.composecleanpermissions.ui.base

import kotlinx.coroutines.flow.SharedFlow

interface EffectDelegate <Effect: Reducer.SideEffect> {
    val effect: SharedFlow<Effect>

    fun sendEffect(effect: Effect)
}