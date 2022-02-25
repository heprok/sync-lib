package com.briolink.lib.sync

import java.time.Instant

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
