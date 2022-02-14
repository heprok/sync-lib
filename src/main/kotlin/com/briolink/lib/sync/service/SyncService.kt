package com.briolink.lib.sync.service

import com.briolink.lib.sync.enumeration.ServiceEnum
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.model.SyncError
import org.springframework.web.reactive.function.client.WebClient

class SyncService(private val webClient: WebClient) {
    private val syncUrl = "sync"

    /**
     * Send completed request to sync-service
     * @param updater name updater who processed
     * @param service name service who sent sync event
     */
    fun sendCompletedSyncAtUpdater(updater: UpdaterEnum, service: ServiceEnum): Boolean {
        val request = webClient.post()
            .uri("/$syncUrl/completed?updater=${updater.name}&service=${service.name}")
            .retrieve()
            .bodyToMono(Void::class.java)
            .block()
        return true
    }

    fun sendSyncErrorAtUpdater(syncError: SyncError): Boolean {
        val request = webClient.post()
            .uri("/$syncUrl/error?syncId=${syncError.syncId}&updater=${syncError.updater.name}&service=${syncError.service.name}&errorText=${syncError.exception}")
            .retrieve()
            .bodyToMono(Void::class.java)
            .block()
        return true
    }
}
