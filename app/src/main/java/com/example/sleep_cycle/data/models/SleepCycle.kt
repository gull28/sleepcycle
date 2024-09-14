package com.example.sleep_cycle.data.models

import com.example.sleep_cycle.data.model.SleepTime
import com.example.sleep_cycle.data.model.Vertice
import com.example.sleep_cycle.ui.components.Vertices

data class SleepCycle(
    val id: Long? = null,
    val name: String,
    val sleepTimes: List<SleepTime> = emptyList(),
    val isActive: Int,
){
    public fun areTimesValid () : Boolean {
        return true
    }

    public fun getSleepTimeVertices(): List<Vertice> {
        val verticeList = mutableListOf<Vertice>()

        sleepTimes.forEach { sleepTime ->
            verticeList.add(sleepTime.getVertice())
        }

        return verticeList
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
