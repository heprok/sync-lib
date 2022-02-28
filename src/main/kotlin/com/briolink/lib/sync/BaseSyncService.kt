package com.briolink.lib.sync

import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import com.briolink.lib.sync.enumeration.ServiceEnum
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.exception.SyncAlreadyRunningException
import com.briolink.lib.sync.model.SyncError
import java.time.Instant

abstract class BaseSyncService() {
    abstract val CURRENT_UPDATER: UpdaterEnum
    abstract val syncLogRepository: ISyncLogRepository<*>
    abstract val syncWebClient: SyncWebClient

    /**
     * Start sync for service if sync at service not exist
     * @param syncId
     * @param service ServiceEnum micro-service that started
     */
    fun startSyncForService(syncId: Int, service: ServiceEnum): Boolean {
        if (syncLogRepository.existsBySyncIdAndService(syncId, service.id)) {
            return false
        }
        when (service) {
            ServiceEnum.User -> startSync(getListSyncLogIdAtUser(syncId))
            ServiceEnum.Company -> startSync(getListSyncLogIdAtCompany(syncId))
            ServiceEnum.CompanyService -> startSync(getListSyncLogIdAtCompanyService(syncId))
            ServiceEnum.Connection -> startSync(getListSyncLogIdAtConnection(syncId))
        }
        return true
    }

    private fun startSync(listSyncLogEntity: List<SyncLogId>) {
        listSyncLogEntity.forEach {
            if (syncLogRepository.existsNotCompleted(it.syncId, it._objectSync, it._service))
                throw SyncAlreadyRunningException(ServiceEnum.ofId(it._service))
        }

        listSyncLogEntity.forEach {
            syncLogRepository.insert(it.syncId, it._service, it._objectSync)
        }
    }

    fun sendSyncError(syncError: SyncError) {
        syncLogRepository.update(syncError.syncId, syncError.service.id, Instant.now(), true)
        syncWebClient.sendSyncErrorAtUpdater(syncError)
    }

    fun completedObjectSync(syncLogId: SyncLogId) {
        syncLogRepository.update(syncLogId.syncId, syncLogId._service, syncLogId._objectSync, Instant.now(), false)

        if (!syncLogRepository.existsNotCompleted(syncLogId.syncId, syncLogId._service))
            syncWebClient.sendCompletedSyncAtUpdater(
                updater = CURRENT_UPDATER,
                service = ServiceEnum.ofId(syncLogId._service)
            )
    }

    fun completedObjectSync(syncId: Int, service: ServiceEnum, objectSync: ObjectSyncEnum) =
        completedObjectSync(
            SyncLogId().apply {
                _objectSync = objectSync.value
                _service = service.id
                this.syncId = syncId
            }
        )

    abstract fun getListSyncLogIdAtCompany(syncId: Int): List<SyncLogId>
    abstract fun getListSyncLogIdAtUser(syncId: Int): List<SyncLogId>
    abstract fun getListSyncLogIdAtCompanyService(syncId: Int): List<SyncLogId>
    abstract fun getListSyncLogIdAtConnection(syncId: Int): List<SyncLogId>
}
