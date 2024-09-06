package com.example.sleep_cycle.data.models

import com.example.sleep_cycle.data.model.SleepTime

data class SleepCycle(
    val id: Long? = null,
    val name: String,
    val sleepTimes: List<SleepTime> = emptyList()


){
    public fun areTimesValid () : Boolean {
        return true
    }
}
