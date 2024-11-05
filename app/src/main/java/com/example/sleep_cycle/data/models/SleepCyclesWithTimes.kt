package com.example.sleep_cycle.data.models

import androidx.room.Embedded
import androidx.room.Relation

data class SleepCycleWithTimes(
    @Embedded val sleepCycle: SleepCycle,
    @Relation(
        parentColumn = "id",
        entityColumn = "scheduleId"
    )
    val sleepTimes: List<SleepTime>
)
