package com.briolink.lib.sync.exception

import com.briolink.lib.sync.enumeration.ServiceEnum

class SyncAlreadyRunningException(service: ServiceEnum) : RuntimeException() {
    override val message: String = "Sync with the service $service is already running"
}
