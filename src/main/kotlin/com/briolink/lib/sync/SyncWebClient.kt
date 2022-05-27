package com.briolink.lib.sync

import com.briolink.lib.sync.enumeration.ServiceEnum
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.model.SyncError
import org.springframework.web.reactive.function.client.WebClient

open class SyncWebClient(private val webClient: WebClient) {
    private val syncUrl = "sync"

    /**
     * Send completed request to sync-service
     * @param updater name updater who processed
     * @param service name service who sent sync event
     */
    open fun sendCompletedSyncAtUpdater(syncId: Int, updater: UpdaterEnum, service: ServiceEnum): Boolean {
        webClient.post()
            .uri { builder ->
                builder.path("/$syncUrl/completed")
                    .queryParam("syncId", syncId)
                    .queryParam("updater", updater.name)
                    .queryParam("service", service.name)
                    .build()
            }
            .retrieve()
            .bodyToMono(Void::class.java)
            .block()
        return true
    }

    open fun sendSyncErrorAtUpdater(syncError: SyncError): Boolean {
        webClient.post()
            .uri { builder ->
                builder.path("/$syncUrl/error")
                    .queryParam("syncId", syncError.syncId)
                    .queryParam("updater", syncError.updater.name)
                    .queryParam("service", syncError.service.name)
                    .queryParam("errorText", syncError.exception.message)
                    .build()
            }
            .retrieve()
            .bodyToMono(Void::class.java)
            .block()
        return true
    }
}
