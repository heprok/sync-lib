package com.briolink.lib.sync.model

import com.briolink.lib.sync.enumeration.PeriodSyncEnum
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

data class PeriodDateTime(val startDateTime: LocalDateTime, val endDateTime: LocalDateTime = LocalDateTime.now()) {
    companion object {
        fun fromEnum(enum: PeriodSyncEnum): PeriodDateTime {
            val currentDateTime = LocalDate.now()
            with(currentDateTime) {
                return when (enum) {
                    PeriodSyncEnum.AllTime -> PeriodDateTime(
                        startDateTime = LocalDateTime.MIN, endDateTime = LocalDateTime.MAX
                    )
                    PeriodSyncEnum.Last2Day -> {
                        val startDateTime = minusDays(2).atStartOfDay()
                        val endDateTime = atTime(23, 59)
                        PeriodDateTime(startDateTime, endDateTime)
                    }
                    PeriodSyncEnum.Last7Day -> {
                        val startDateTime = minusDays(7).atStartOfDay()
                        val endDateTime = atTime(23, 59)
                        PeriodDateTime(startDateTime, endDateTime)
                    }
                    PeriodSyncEnum.Last30Day -> {
                        val startDateTime = minusDays(30).atStartOfDay()
                        val endDateTime = atTime(23, 59)
                        PeriodDateTime(startDateTime, endDateTime)
                    }
                    PeriodSyncEnum.CurrentMouth -> {
                        val startDateTime = with(TemporalAdjusters.firstDayOfMonth()).atTime(0, 0)
                        val endDateTime = with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59)
                        PeriodDateTime(startDateTime, endDateTime)
                    }
                    PeriodSyncEnum.LastMouth -> {
                        val startDateTime = minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).atTime(0, 0)
                        val endDateTime = minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59)
                        PeriodDateTime(startDateTime, endDateTime)
                    }
                }
            }
        }
    }
}
