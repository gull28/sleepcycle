package com.example.sleep_cycle.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Ignore
import com.example.sleep_cycle.helpers.Time
import java.util.Calendar

@Entity(tableName = "sleep_cycles")
data class SleepCycle(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    var isActive: Int = 0
) {
    @Ignore
    var sleepTimes: List<SleepTime> = emptyList()

    /**
     * Checks if the times in the sleep cycle are valid.
     *
     * @return `true` if all times are valid, otherwise `false`.
     */
    fun areTimesValid(): Boolean {
        return true
    }

    /**
     * Retrieves the next upcoming sleep time based on the current time.
     *
     * @return The next `SleepTime` instance if available, otherwise `null`.
     */
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

    /**
     * Converts each sleep time in the cycle into a list of `Vertice` objects.
     *
     * @return A list of `Vertice` objects representing each sleep time.
     */
    fun getSleepTimeVertices(): List<Vertice> {
        return sleepTimes.map { it.getVertice() }
    }

    /**
     * Calculates the total sleep time for the cycle by summing up the duration
     * of each sleep time.
     *
     * @return Total duration in minutes.
     */
    fun totalSleepTime(): Int {
        return sleepTimes.sumOf { it.duration }
    }
}
