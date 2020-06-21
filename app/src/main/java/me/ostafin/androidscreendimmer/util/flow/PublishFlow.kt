package me.ostafin.androidscreendimmer.util.flow

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.conflate

class PublishFlow<T> : Flow<T> {

    private val channel: BroadcastChannel<T> = BroadcastChannel(1)

    fun emit(value: T) {
        val result = channel.offer(value)
        require(result) {
            "result must be true"
        }
    }

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<T>) {
        channel.asFlow().conflate().collect(collector)
    }

}