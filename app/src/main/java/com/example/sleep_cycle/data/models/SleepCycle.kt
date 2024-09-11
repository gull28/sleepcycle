package com.example.sleep_cycle.data.models

import com.example.sleep_cycle.data.model.SleepTime

data class SleepCycle(
    val id: Long? = null,
    val name: String,
    val sleepTimes: List<SleepTime> = emptyList(),
    val isActive: Int,
){
    public fun areTimesValid () : Boolean {
        return true
    }

    // Returns total sleeptime in minutes (!!)
    public fun totalSleepTime() : Int {
        var totalTime: Int = 0;
        sleepTimes.map { sleepTime ->
            totalTime += sleepTime.duration
        }

        return totalTime
    }
}
