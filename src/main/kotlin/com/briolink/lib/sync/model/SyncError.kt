package com.briolink.lib.sync.model

import com.fasterxml.jackson.annotation.JsonProperty

data class SyncError(
    @JsonProperty
    val syncServiceId: Int,
    @JsonProperty
    val exception: Exception,
    @JsonProperty
    val rowId: Int
) {
    override fun toString(): String {
        return exception.message + " " + exception.stackTraceToString()
    }
}
