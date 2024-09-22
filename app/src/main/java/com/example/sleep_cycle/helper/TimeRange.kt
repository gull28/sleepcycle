import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.Calendar
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class TimeRange(private val startTimeStr: String, private val endTimeStr: String) {

    private val startTime = parseTime(startTimeStr)
    private val endTime = parseTime(endTimeStr)

    private fun parseTime(timeStr: String): Pair<Int, Int> {
        val (hours, minutes) = timeStr.split(":").map { it.toInt() }
        return Pair(hours, minutes)
    }

    private fun toMinutes(time: Pair<Int, Int>): Int {
        return time.first * 60 + time.second
    }

    fun isWithinRange(timeStr: String): Boolean {
        val time = parseTime(timeStr)
        val currentTimeInMinutes = toMinutes(time)
        val startTimeInMinutes = toMinutes(startTime)
        val endTimeInMinutes = toMinutes(endTime)

        return if (endTimeInMinutes < startTimeInMinutes) {
            currentTimeInMinutes >= startTimeInMinutes || currentTimeInMinutes < endTimeInMinutes
        } else {
            currentTimeInMinutes in (startTimeInMinutes until endTimeInMinutes)
        }
    }

    fun millisUntilEnd(currentTimeStr: String): Long {
        val currentTime = parseTime(currentTimeStr)

        val currentTimeInMinutes = toMinutes(currentTime)
        val startTimeInMinutes = toMinutes(startTime)
        val endTimeInMinutes = toMinutes(endTime)

        val durationInMinutes: Int

        if (endTimeInMinutes < startTimeInMinutes) {

            if (currentTimeInMinutes >= startTimeInMinutes) {
                durationInMinutes = (1440 - currentTimeInMinutes) + endTimeInMinutes
            } else {
                durationInMinutes = endTimeInMinutes - currentTimeInMinutes
            }
        } else {

            durationInMinutes = endTimeInMinutes - currentTimeInMinutes
        }

        val durationInMillis = durationInMinutes * 60 * 1000L
        return durationInMillis
    }


    override fun toString(): String {
        return "TimeRange(startTime=$startTimeStr, endTime=$endTimeStr)"
    }
}
