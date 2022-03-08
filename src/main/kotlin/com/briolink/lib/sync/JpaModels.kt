package com.briolink.lib.sync

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.io.Serializable
import java.time.Instant
import javax.persistence.Embeddable

interface ISyncLogRepository<T : ISyncLogEntity> {
    @Query("""SELECT count(s) > 0 FROM #{#entityName} s WHERE s.id.syncId = ?1 AND s.id._objectSync = ?2 AND s.id._service = ?3 AND s.completed IS NULL""") // ktlint-disable max-line-length
    fun existsNotCompleted(syncId: Int, objectSync: Int, serviceId: Int): Boolean

    @Query("""SELECT s FROM #{#entityName} s WHERE s.id.syncId = ?1 AND s.id._service = ?2""")
    fun findBySyncIdAndService(syncId: Int, serviceId: Int): List<T>

    @Query("SELECT count(s) > 0 FROM #{#entityName} s WHERE s.id.syncId = ?1 AND s.id._service = ?2 AND s.completed IS NULL OR s.withError = true") // ktlint-disable max-line-length
    fun existsNotCompleted(syncId: Int, serviceId: Int): Boolean

    @Query("SELECT count(s) > 0 FROM #{#entityName} s WHERE s.id.syncId = ?1 AND s.id._service = ?2")
    fun existsBySyncIdAndService(syncId: Int, serviceId: Int): Boolean

    /*
     * Method must be @query and @Modify
     * @query must be contain 'ON CONFLICT DO NOTHING'
     */
    @Modifying
    @Query(
        "INSERT INTO read.sync_log(sync_id, service, object_sync) VALUES(?1, ?2, ?3) ON CONFLICT DO NOTHING",
        nativeQuery = true
    )
    fun insert(syncId: Int, serviceId: Int, objectSync: Int)

    @Modifying
    @Query("UPDATE #{#entityName} s SET s.completed = ?3, s.withError = ?4 WHERE s.id._objectSync = ?5 AND s.id._service = ?2 AND s.id.syncId = ?1")
    fun update(syncId: Int, serviceId: Int, completed: Instant?, withError: Boolean, objectSync: Int)

    @Modifying
    @Query("UPDATE #{#entityName} s SET s.completed = ?4, s.withError = ?5 WHERE s.id.syncId = ?1 AND s.id._objectSync = ?3 AND s.id._service = ?2")
    fun update(syncId: Int, serviceId: Int, objectSync: Int, completed: Instant?, withError: Boolean)
}

interface BaseTimeMarkRepository<T> {
    fun findAll(pageable: Pageable): Page<T>
    @Query("SELECT #{#entityName} s WHERE c.created BETWEEN ?1 AND ?2 OR c.changed BETWEEN ?1 AND ?2")
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
