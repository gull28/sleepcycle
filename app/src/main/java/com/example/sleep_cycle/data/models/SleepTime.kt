package com.example.sleep_cycle.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class SleepTime(
    val id: Long? = null,
    var name: String = "",
    val scheduleId: Long? = null,
    var startTime: String = "",
    val duration: Int = 20,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateEndTime(): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val start = LocalTime.parse(startTime, formatter)
        val end = start.plusMinutes(duration.toLong())
        return end.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isTimeInTimeFrame(timeStart: String, timeEnd: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val start = LocalTime.parse(startTime, formatter)
        val end = start.plusMinutes(duration.toLong())

        val startTimeToCheck = LocalTime.parse(timeStart, formatter)
        val endTimeToCheck = LocalTime.parse(timeEnd, formatter)

        return startTimeToCheck.isBefore(end) && endTimeToCheck.isAfter(start)
    }
}
