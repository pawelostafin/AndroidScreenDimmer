package me.ostafin.androidscreendimmer.util.flow

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow
import me.ostafin.androidscreendimmer.util.NO_VALUE

class BehaviourFlow<T>(initialValue: T = NO_VALUE as T) : Flow<T> {

    private val channel: ConflatedBroadcastChannel<T> = if (initialValue == NO_VALUE) {
        ConflatedBroadcastChannel()
    } else {
        ConflatedBroadcastChannel(initialValue)
    }

    fun emit(value: T) {
        val result = channel.offer(value)
        require(result) {
            "result must be true"
        }
    }

    val valueOrNull: T?
        get() = channel.valueOrNull

    val requireValue: T
        get() = channel.valueOrNull ?: error("not initialized")

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<T>) {
        channel.asFlow().collect(collector)
    }

}