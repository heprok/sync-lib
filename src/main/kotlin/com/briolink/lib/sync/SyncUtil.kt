package com.briolink.lib.sync

import com.briolink.lib.sync.model.PeriodDateTime
import org.springframework.data.domain.PageRequest

class SyncUtil {
    companion object {
        fun <R : BaseTimeMarkRepository<*>> publishSyncEvent(
            period: PeriodDateTime?,
            repository: R,
            funPublishEvent: (Long, Long, Any?) -> Unit
        ) {
            var pageRequest = PageRequest.of(0, 200)
            var page = if (period == null) repository.findAll(pageRequest)
            else repository.findByPeriod(period.startInstants, period.endInstant, pageRequest)
            if (page.totalElements.toInt() == 0) {
                funPublishEvent(1, 1, null)
                return
            }
            var indexObjectSync = 0
            while (!page.isEmpty) {
                pageRequest = pageRequest.next()
                page.content.forEach {
                    indexObjectSync += 1
                    funPublishEvent(indexObjectSync.toLong(), page.totalElements, it)
                }
                page = if (period == null) repository.findAll(pageRequest)
                else repository.findByPeriod(period.startInstants, period.endInstant, pageRequest)
            }
        }
    }
}
