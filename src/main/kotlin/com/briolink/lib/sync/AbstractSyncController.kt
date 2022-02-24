package com.briolink.lib.sync

import com.briolink.lib.sync.model.PeriodDateTime
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

abstract class AbstractSyncController {
    /**
     *  HTTP method must be POST
     *  @param startLocalDateTime format ISO_DATE_TIME at UTC
     *  @param endLocalDateTime format ISO_DATE_TIME at UTC
     *  @param syncId Int
     */
    fun sync(
        startLocalDateTime: String? = null,
        endLocalDateTime: String? = null,
        syncId: Int
    ): ResponseEntity<Any> {
        val periodLocalDateTime = if (startLocalDateTime != null && endLocalDateTime != null) PeriodDateTime(
            startDateTime = LocalDateTime.parse(startLocalDateTime), endDateTime = LocalDateTime.parse(endLocalDateTime)
        ) else null
        publishSyncEvent(syncId, periodLocalDateTime)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    /**
     * Sync event logic for publish to SNS
     * The function must be async for the response
     */
    abstract fun publishSyncEvent(syncId: Int, period: PeriodDateTime?)
}
