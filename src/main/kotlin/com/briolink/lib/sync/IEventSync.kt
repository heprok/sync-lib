package com.briolink.lib.sync

import com.briolink.lib.sync.enumeration.ServiceEnum

interface IEventSync<T> {
    val service: ServiceEnum
    val syncId: Int
    val indexRow: Long

    /**
     * true if this data is the latest to sync from entities
     */
    val isLastData: Boolean
    val totalElements: Long
    val data: T
}
