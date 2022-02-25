package com.briolink.lib.sync

import com.briolink.lib.sync.enumeration.ServiceEnum

interface ISyncData<T> {
    val service: ServiceEnum
    val syncId: Int
    val indexObjectSync: Long
    val totalObjectSync: Long
    val objectSync: T?
}
