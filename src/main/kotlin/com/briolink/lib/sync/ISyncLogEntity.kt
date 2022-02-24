package com.briolink.lib.sync

import java.time.Instant

interface ISyncLogEntity {
    val id: SyncLogId
    var completed: Instant?
    var withError: Boolean
}
