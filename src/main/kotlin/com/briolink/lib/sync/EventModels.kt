package com.briolink.lib.sync

import com.briolink.event.Event
import com.briolink.lib.sync.enumeration.ServiceEnum
import com.fasterxml.jackson.annotation.JsonProperty

interface ISyncData<T> {
    val service: ServiceEnum
    val syncId: Int
    val objectIndex: Long
    val totalObjects: Long
    val objectSync: T?
}

data class SyncData<T>(
    @JsonProperty
    override val service: ServiceEnum,
    @JsonProperty
    override val syncId: Int,
    @JsonProperty
    override val objectIndex: Long,
    @JsonProperty
    override val totalObjects: Long,
    @JsonProperty
    override val objectSync: T?,
) : ISyncData<T>

abstract class SyncEvent<T>(override val version: String) : Event<SyncData<T>>(version) {
    abstract override val data: SyncData<T>
    override val name: String = this.javaClass.simpleName
    override val timestamp: Long = System.currentTimeMillis()
}
