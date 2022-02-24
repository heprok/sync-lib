package com.briolink.lib.sync

import java.io.Serializable
import javax.persistence.Embeddable

@Embeddable
class SyncLogId() : Serializable {
    var syncId: Int = -1
    var _objectSync: Int = -1
    var _service: Int = -1
}
