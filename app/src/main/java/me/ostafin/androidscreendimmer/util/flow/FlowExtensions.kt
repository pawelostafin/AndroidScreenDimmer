package me.ostafin.androidscreendimmer.util.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

fun BehaviourFlow<Unit>.emit() = emit(Unit)
fun PublishFlow<Unit>.emit() = emit(Unit)

fun <T> Flow<T>.addLoadingHandling(loadingFlow: BehaviourFlow<Boolean>) {
    onStart { loadingFlow.emit(true) }
    onEach { loadingFlow.emit(false) }
    onCompletion { loadingFlow.emit(false) }
}