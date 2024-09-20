package com.example.sleep_cycle.data.models

import android.util.Log
import com.example.sleep_cycle.data.model.SleepTime
import com.example.sleep_cycle.data.model.Vertice
import com.example.sleep_cycle.helper.Time
import com.example.sleep_cycle.ui.components.Vertices
import java.util.Calendar

data class SleepCycle(
    val id: Long? = null,
    val name: String,
    val sleepTimes: List<SleepTime> = emptyList(),
    val isActive: Int,
){
    public fun areTimesValid () : Boolean {
        return true
    }

    fun getNextSleepTime(): SleepTime? {
        val currentTime = Calendar.getInstance()

        val futureSleepTimes = sleepTimes.map { sleepTime ->
            val sleepStartTime = Time.stringToDateObj(sleepTime.startTime)

            if (sleepStartTime.before(currentTime)) {
                sleepStartTime.add(Calendar.DAY_OF_YEAR, 1)
            }

            Pair(sleepTime, sleepStartTime.timeInMillis)
        }

        return futureSleepTimes.minByOrNull { it.second }?.first
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
