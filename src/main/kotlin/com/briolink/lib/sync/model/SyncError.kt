package com.briolink.lib.sync.model

import com.briolink.lib.sync.enumeration.ServiceEnum
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.fasterxml.jackson.annotation.JsonProperty

data class SyncError(
    @JsonProperty
    val syncId: Int,
    @JsonProperty
    val updater: UpdaterEnum,
    @JsonProperty
    val service: ServiceEnum,
    @JsonProperty
    val exception: Exception,
    @JsonProperty
    val indexRow: Long
) {
    override fun toString(): String {
        return exception.message + " " + exception.stackTraceToString()
    }
}
