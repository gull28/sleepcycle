package com.example.sleep_cycle.data.models

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.sleep_cycle.helper.Time
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

interface Vertice {
    var start: Int;
    var end: Int;
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
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "scheduleId", index = true) var scheduleId: Long? = null, // Foreign key
    @ColumnInfo(name = "start_time") var startTime: String = "",
    @ColumnInfo(name = "duration") val duration: Int = 20
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateEndTime(): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val start = LocalTime.parse(startTime, formatter)
        val end = start.plusMinutes(duration.toLong())
        return end.format(formatter)
    }

    fun getVertice(): Vertice {
        val start = Time.stringToDateObj(startTime)
        val end = start.clone() as Calendar
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

    fun getEndTime(): Calendar {
        val startCal = Time.stringToDateObj(startTime)
        val endTime = startCal.clone() as Calendar

        endTime.add(Calendar.MINUTE, duration)

        if (startCal.after(endTime)) {
            startCal.add(Calendar.DAY_OF_YEAR, -1)
        }

        return endTime
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isTimeInTimeFrame(timeStart: String, timeEnd: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val start = LocalTime.parse(startTime, formatter)
        val end = start.plusMinutes(duration.toLong())

        val startTimeToCheck = LocalTime.parse(timeStart, formatter)
        val endTimeToCheck = LocalTime.parse(timeEnd, formatter)

        val isExistingCrossingMidnight = start.isAfter(end)
        val isNewCrossingMidnight = startTimeToCheck.isAfter(endTimeToCheck)

        // if both cross midnight, both overlap
        if (isExistingCrossingMidnight && isNewCrossingMidnight) {
            return true
        }

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
