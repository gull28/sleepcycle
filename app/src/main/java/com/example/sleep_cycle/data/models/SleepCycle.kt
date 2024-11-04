package com.example.sleep_cycle.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Ignore

import com.example.sleep_cycle.helpers.Time
import java.util.Calendar

@Entity(tableName = "sleep_cycles")
data class SleepCycle(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,  // Primary key for Room

    val name: String,
    var isActive: Int = 0
) {
    @Ignore
    var sleepTimes: List<SleepTime> = emptyList()

    fun areTimesValid(): Boolean {
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

    fun getSleepTimeVertices(): List<Vertice> {
        val verticeList = mutableListOf<Vertice>()

        sleepTimes.forEach { sleepTime ->
            verticeList.add(sleepTime.getVertice())
        }

        return verticeList
    }

    fun totalSleepTime(): Int {
        var totalTime = 0
        sleepTimes.forEach { sleepTime ->
            totalTime += sleepTime.duration
        }

        return totalTime
    }
}
