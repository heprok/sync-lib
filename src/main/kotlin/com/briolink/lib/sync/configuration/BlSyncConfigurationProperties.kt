package com.briolink.lib.sync.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "sync-service.api")
@Suppress("ConfigurationProperties")
data class BlSyncConfigurationProperties(
    val url: String,
    val version: String
)
