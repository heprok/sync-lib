package com.briolink.lib.sync

import com.briolink.lib.event.IEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import com.briolink.lib.sync.model.SyncError

abstract class SyncEventHandler<X : SyncEvent<*>>(
    private val objectSync: ObjectSyncEnum,
    val syncService: BaseSyncService
) : IEventHandler<X> {
    /**
     * Start local sync from service,
     * if objectSync is null then local sync start and will end
     * If objectSync is not null then sync started
     * @return true if object sync is not null
     * @return false if  object sync is null
     */
    fun objectSyncStarted(syncData: ISyncData<*>): Boolean {
        if (syncData.objectIndex.toInt() == 1)
            syncService.startSyncForService(syncData.syncId, syncData.service)

        if (syncData.objectSync == null) {
            syncService.completedObjectSync(syncData.syncId, syncData.service, objectSync)
            return false
        }
        return true
    }

    fun sendError(syncData: ISyncData<*>, ex: Exception) {
        syncService.sendSyncError(
            syncError = SyncError(
                objectSync = objectSync,
                service = syncData.service,
                updater = syncService.CURRENT_UPDATER,
                syncId = syncData.syncId,
                exception = ex,
                indexObjectSync = syncData.objectIndex,
            ),
        )
    }

    /**
     * Check syncData if syncObject last then completedObjectSync
     * @return false if sync not finished
     * @return true if sync finished from objectSync
     */
    fun objectSyncCompleted(syncData: ISyncData<*>): Boolean {
        if (syncData.objectIndex == syncData.totalObjects) {
            syncService.completedObjectSync(syncData.syncId, syncData.service, objectSync)
            return true
        }
        return false
    }
}
