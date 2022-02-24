package com.briolink.lib.sync

import com.briolink.lib.sync.enumeration.ServiceEnum
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.exception.SyncAlreadyRunningException

abstract class AbstractSyncService(
    private val syncWebClient: SyncWebClient,
    private val syncLogRepository: ISyncLogRepository
) {
    abstract val CURRENT_UPDATER: UpdaterEnum

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
            ServiceEnum.User -> startSyncAtUser(syncId)
            ServiceEnum.Company -> startSyncAtCompany(syncId)
            ServiceEnum.CompanyService -> startSyncAtCompanyService(syncId)
            ServiceEnum.Connection -> startSyncAtConnection(syncId)
        }
        return true
    }

    private fun startSync(listSyncLogEntity: List<ISyncLogEntity>): List<ISyncLogEntity> {
        listSyncLogEntity.forEach {
            if (syncLogRepository.existsNotCompleted(it.id.syncId, it.id._objectSync, it.id._service))
                throw SyncAlreadyRunningException(ServiceEnum.ofId(it.id._service))
        }

        mutableListOf<ISyncLogEntity>().apply {
            listSyncLogEntity.forEach {
                add(syncLogRepository.save(it))
            }
            return this
        }
    }

    abstract fun startSyncAtCompany(syncId: Int): List<ISyncLogEntity>
    abstract fun startSyncAtUser(syncId: Int): List<ISyncLogEntity>
    abstract fun startSyncAtCompanyService(syncId: Int): List<ISyncLogEntity>
    abstract fun startSyncAtConnection(syncId: Int): List<ISyncLogEntity>
}
