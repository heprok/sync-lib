package com.briolink.lib.sync.configuration

import com.briolink.lib.sync.service.SyncService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@ComponentScan("com.briolink.lib.sync.service")
@EnableConfigurationProperties(
    BlSyncConfigurationProperties::class
)
@ConditionalOnProperty(prefix = "sync-service.api", name = ["url", "version"])
class BlSyncServiceAutoConfiguration {
    @Value("\${sync-service.api.url}")
    lateinit var urlApi: String

    @Value("\${sync-service.api.version}")
    lateinit var apiVersion: String

    @Bean
    @Primary
    fun syncService() = SyncService(WebClient.create("$urlApi/api/v$apiVersion/"))
}
