package com.example.sleep_cycle.helpers

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale

interface TimeUntil {
    var minutes: Int
    var seconds: Int
}

class Time(private val minutes: Int) {

    companion object {
        const val degreesPerHour = 360.0 / 24
        const val degreesPerMinute = degreesPerHour / 60

        /**
         * Converts a given duration in minutes to an "HH:mm" formatted string.
         *
         * @param minutes The total duration in minutes.
         * @return A string formatted as "HH:mm".
         */
        fun minutesToHHMM(minutes: Int): String {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            return String.format("%02d:%02d", hours, remainingMinutes)
        }

        /**
         * Checks if the given time interval crosses midnight.
         *
         * @param start The start time as `LocalTime`.
         * @param end The end time as `LocalTime`.
         * @return `true` if the interval crosses midnight, otherwise `false`.
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun isPassingMidnight(start: LocalTime, end: LocalTime): Boolean {
            return end.isBefore(start)
        }

        /**
         * Calculates the time remaining from the current time to a specified future time.
         * If the specified time is earlier than the current time, it adjusts to the next day.
         *
         * @param timeInFuture A `Calendar` instance representing the future time.
         * @return The remaining time in milliseconds from the current time to `timeInFuture`.
         */
        fun getTimeUntil(timeInFuture: Calendar): Long {
            val currentTime = Calendar.getInstance()

            if (timeInFuture.before(currentTime)) {
                timeInFuture.add(Calendar.DATE, 1)  // Adjust the date if it's in the past
            }

            return timeInFuture.timeInMillis - currentTime.timeInMillis
        }

        /**
         * Parses a time string in "HH:mm:ss" format into a `Calendar` object.
         *
         * @param time A string representing the time in "HH:mm:ss" format.
         * @return A `Calendar` instance set to the parsed time.
         */
        fun stringToDateObj(time: String): Calendar {
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(time)

            return Calendar.getInstance().apply {
                if (date != null) {
                    val parsedCalendar = Calendar.getInstance().apply {
                        this.time = date
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
