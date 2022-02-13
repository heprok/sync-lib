package com.briolink.lib.sync

import com.briolink.lib.sync.enumeration.ServiceEnum

interface ISyncEvent {
    val service: ServiceEnum
    val syncServiceId: Int
    val rowId: Int
    val totalRow: Int
}
