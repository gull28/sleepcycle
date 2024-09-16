// Time.kt
package com.example.sleep_cycle.helper

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


interface TimeUntil {
    var minutes: Int;
    var seconds: Int;
}

class Time(private val minutes: Int) {

    companion object {
        public val degreesPerHour = 360.0 / 24
        public val degreesPerMinute = degreesPerHour / 60

        fun minutesToHHMM(minutes: Int): String {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            return String.format("%02d:%02d", hours, remainingMinutes)
        }

        fun HHMMtoMinutes(strTime : String): Int {
            return 0
        }

        fun getTimeUntil(timeInFuture: Calendar): String {
            val currentTime = Calendar.getInstance()

            if (timeInFuture.before(currentTime)) {
                timeInFuture.add(Calendar.DATE, 1)
            }

            val diffInMillis = timeInFuture.timeInMillis - currentTime.timeInMillis

            val diffInMinutes = diffInMillis / (1000 * 60)
            val hours = diffInMinutes / 60
            val minutes = diffInMinutes % 60

            return String.format("%02d:%02d", hours, minutes)
        }

        fun calendarToString(calendar: Calendar): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(calendar.time)
        }


        fun stringToDateObj(minutes: String): Calendar {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            // Parse the time part of the string
            val date = dateFormat.parse(minutes)

            // Get today's date
            return Calendar.getInstance().apply {
                if (date != null) {
                    // Get the hours and minutes from the parsed time
                    val parsedCalendar = Calendar.getInstance().apply {
                        time = date
                    }
                    // Set the hours and minutes to today's date
                    set(Calendar.HOUR_OF_DAY, parsedCalendar.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, parsedCalendar.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
            }
        }

    }
}
