package com.example.sleep_cycle.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.sleep_cycle.helper.Time
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

interface Vertice {
    var start: Int;
    var end: Int;
}

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

    fun getVertice(): Vertice {
        val start = Time.stringToDateObj(startTime)
        val end = start.clone() as Calendar;
        end.add(Calendar.MINUTE, duration)

        val startHour = start.get(Calendar.HOUR_OF_DAY)
        val startMinute = start.get(Calendar.MINUTE)

        val endHour = end.get(Calendar.HOUR_OF_DAY)
        val endMinute = end.get(Calendar.MINUTE)

        val startVertice = (Time.degreesPerHour * startHour) + (Time.degreesPerMinute * startMinute)
        val endVertice = (Time.degreesPerHour * endHour) + (Time.degreesPerMinute * endMinute)

        return object : Vertice {
            override var start: Int = startVertice.toInt()
            override var end: Int = endVertice.toInt()
        }
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
