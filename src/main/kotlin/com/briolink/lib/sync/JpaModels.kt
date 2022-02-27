package com.briolink.lib.sync

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.io.Serializable
import java.time.Instant
import javax.persistence.Embeddable

interface ISyncLogRepository {
    fun existsNotCompleted(syncId: Int, objectSync: Int, serviceId: Int): Boolean
    fun findBySyncIdAndService(syncId: Int, serviceId: Int): List<ISyncLogEntity>
    fun existsNotCompleted(syncId: Int, serviceId: Int): Boolean
    fun existsBySyncIdAndService(syncId: Int, serviceId: Int): Boolean

    /*
     * Method must be @query and @Modify
     * @query must be contain 'ON CONFLICT DO NOTHING'
     */
    fun insert(syncId: Int, serviceId: Int, objectSync: Int)
    fun update(syncId: Int, serviceId: Int, objectSync: Int, completed: Instant?, withError: Boolean)
    fun update(syncId: Int, serviceId: Int, completed: Instant?, withError: Boolean)
}

interface BaseTimeMarkRepository<T> {
    fun findAll(pageable: Pageable): Page<T>
    fun findByPeriod(start: Instant, end: Instant, pageable: Pageable): Page<T>
}

interface ISyncLogEntity {
    val id: SyncLogId
    var completed: Instant?
    var withError: Boolean
}

@Embeddable
class SyncLogId() : Serializable {
    var syncId: Int = -1
    var _objectSync: Int = -1
    var _service: Int = -1
}
