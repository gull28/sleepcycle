// Time.kt
package com.example.sleep_cycle.helper

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

        fun stringToDateObj(minutes: String): Calendar {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            // Parse the date to a Date object
            val date = dateFormat.parse(minutes)
            // Create a Calendar instance and set the time
            return Calendar.getInstance().apply {
                time = date
            }
        }
    }
}
