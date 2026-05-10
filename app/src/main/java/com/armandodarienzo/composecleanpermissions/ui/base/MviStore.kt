package com.armandodarienzo.composecleanpermissions.ui.base

import kotlinx.coroutines.flow.StateFlow

interface MviStore<State: Reducer.ViewState, Event: Reducer.ViewEvent> {
    val state: StateFlow<State>
    val timeCapsule: TimeCapsule<State>
    fun sendEvent(event: Event)
}