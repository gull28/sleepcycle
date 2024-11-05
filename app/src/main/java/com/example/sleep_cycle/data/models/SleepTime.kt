package com.example.sleep_cycle.data.models

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.sleep_cycle.helpers.Time
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

interface Vertice {
    var start: Int
    var end: Int
}

@Entity(
    tableName = "sleep_times",
    foreignKeys = [ForeignKey(
        entity = SleepCycle::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("scheduleId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class SleepTime(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "scheduleId", index = true) var scheduleId: Long? = null, // Foreign key
    @ColumnInfo(name = "start_time") var startTime: String = "",
    @ColumnInfo(name = "duration") var duration: Int = 20
) {
    /**
     * Calculates the end time based on the start time and duration.
     *
     * @return The calculated end time in "HH:mm:ss" format.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateEndTime(): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val start = LocalTime.parse(startTime, formatter)
        val end = start.plusMinutes(duration.toLong())
        return end.format(formatter)
    }

    /**
     * Converts the start and end times of this `SleepTime` instance into a `Vertice` object.
     *
     * @return A `Vertice` representing the angle positions of the start and end times on a 24-hour clock.
     */
    fun getVertice(): Vertice {
        val start = Time.stringToDateObj(startTime)
        val end = start.clone() as Calendar
        end.add(Calendar.MINUTE, duration)

        val startVertice = (Time.degreesPerHour * start.get(Calendar.HOUR_OF_DAY)) +
                (Time.degreesPerMinute * start.get(Calendar.MINUTE))
        val endVertice = (Time.degreesPerHour * end.get(Calendar.HOUR_OF_DAY)) +
                (Time.degreesPerMinute * end.get(Calendar.MINUTE))

        return object : Vertice {
            override var start: Int = startVertice.toInt()
            override var end: Int = endVertice.toInt()
        }
    }

    /**
     * Calculates the exact end time as a `Calendar` object.
     * Adjusts for cases where the end time crosses over into the next day.
     *
     * @return A `Calendar` object representing the end time.
     */
    fun getEndTime(): Calendar {
        val startCal = Time.stringToDateObj(startTime)
        val endTime = startCal.clone() as Calendar
        endTime.add(Calendar.MINUTE, duration)

        if (startCal.after(endTime)) {
            startCal.add(Calendar.DAY_OF_YEAR, -1)
        }

        return endTime
    }

    /**
     * Checks if a given time frame (defined by `timeStart` and `timeEnd`) overlaps with this sleep time.
     *
     * @param timeStart The start of the time frame to check, in "HH:mm:ss" format.
     * @param timeEnd The end of the time frame to check, in "HH:mm:ss" format.
     * @return `true` if the specified time frame overlaps with this sleep time, otherwise `false`.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun isTimeInTimeFrame(timeStart: String, timeEnd: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val start = LocalTime.parse(startTime, formatter)
        val end = start.plusMinutes(duration.toLong())

        val startTimeToCheck = LocalTime.parse(timeStart, formatter)
        val endTimeToCheck = LocalTime.parse(timeEnd, formatter)

        val isExistingCrossingMidnight = start.isAfter(end)
        val isNewCrossingMidnight = startTimeToCheck.isAfter(endTimeToCheck)

        if (start == startTimeToCheck || end == endTimeToCheck) return true

        if (isExistingCrossingMidnight && isNewCrossingMidnight) return true

        if (isNewCrossingMidnight) {
            return (start.isBefore(endTimeToCheck) || end.isBefore(endTimeToCheck)) ||
                    (start.isAfter(startTimeToCheck) || end.isAfter(startTimeToCheck))
        }

        // only the existing interval crosses midnight
        if (isExistingCrossingMidnight) {
            return (startTimeToCheck.isBefore(end) || endTimeToCheck.isBefore(end)) ||
                    (startTimeToCheck.isAfter(start) || endTimeToCheck.isAfter(start))
        }

        // neither time crosses midnight — standard overlap check
        // check if startTimeToCheck/endTimeToCheck is within start/end time frame
        // check if startTimeToCheck/endTimeToCheck envelops start/end
        return (startTimeToCheck.isBefore(end) && startTimeToCheck.isAfter(start)) ||
                (endTimeToCheck.isBefore(end) && endTimeToCheck.isAfter(start)) ||
                (startTimeToCheck.isBefore(start) && endTimeToCheck.isAfter(end))
    }
}
