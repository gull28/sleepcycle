// Time.kt
package com.example.sleep_cycle.helper

class Time(private val minutes: Int) {

    companion object {
        fun minutesToHHMM(minutes: Int): String {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            return String.format("%02d:%02d", hours, remainingMinutes)
        }

        fun HHMMtoMinutes(strTime : String): Int {
            return 0
        }
    }
}
