// Time.kt
package com.example.sleep_cycle.helpers

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale


interface TimeUntil {
    var minutes: Int;
    var seconds: Int;
}

class Time(private val minutes: Int) {

    companion object {
        const val degreesPerHour = 360.0 / 24
        const val degreesPerMinute = degreesPerHour / 60

        fun minutesToHHMM(minutes: Int): String {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            val seconds = 0

            return String.format("%02d:%02d", hours, remainingMinutes, seconds)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun isPassingMidnight(start: LocalTime, end: LocalTime): Boolean {
            return end.isBefore(start)
        }

        fun getTimeUntil(timeInFuture: Calendar): Long {
            val currentTime = Calendar.getInstance()

            if (timeInFuture.before(currentTime)) {
                timeInFuture.add(Calendar.DATE, 1)  // Adjust the date if it's in the past
            }

            val diffInMillis = timeInFuture.timeInMillis - currentTime.timeInMillis

            return diffInMillis
        }



        fun stringToDateObj(minutes: String): Calendar {
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(minutes)

            return Calendar.getInstance().apply {
                if (date != null) {
                    val parsedCalendar = Calendar.getInstance().apply {
                        time = date
                    }

                    set(Calendar.HOUR_OF_DAY, parsedCalendar.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, parsedCalendar.get(Calendar.MINUTE))
                    set(Calendar.SECOND, parsedCalendar.get(Calendar.SECOND))
                    set(Calendar.MILLISECOND, 0)
                }
            }
        }

    }
}
