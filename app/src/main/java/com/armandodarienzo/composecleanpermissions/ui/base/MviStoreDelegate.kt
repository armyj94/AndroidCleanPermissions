package com.armandodarienzo.composecleanpermissions.ui.base

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MviStoreDelegate<State : Reducer.ViewState, Event : Reducer.ViewEvent, Effect : Reducer.SideEffect>(
    initialState: State,
    private val scope: CoroutineScope,
    private val reducer: Reducer<State, Event, Effect>,
    private val effectDelegate: StandardEffectDelegate<Effect>, // Dependency on EffectCarrier
    private val initialDataLoad: (suspend () -> Unit)? = null
) : MviStore<State, Event> {

    private val _state = MutableStateFlow(initialState)

    override val timeCapsule: TimeCapsule<State> = TimeTravelCapsule { storedState ->
        _state.tryEmit(storedState)
    }

    init {
        timeCapsule.addState(initialState)
    }

    override val state: StateFlow<State> by lazy {
        _state.onStart {
            if (initialDataLoad != null) {
                scope.launch { initialDataLoad.invoke() }
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = initialState
        )
    }

    override fun sendEvent(event: Event) {
        reduceAndEmit(event)
    }

    private fun reduceAndEmit(event: Event) {
        val (newState, sideEffect) = reducer.reduce(_state.value, event)

        val success = _state.tryEmit(newState)

        if (success) {
            timeCapsule.addState(newState)
        }

        if (sideEffect != null) {
            effectDelegate.sendEffect(sideEffect)
        }
    }
}