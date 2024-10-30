import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class TimeRange(private val startTimeStr: String, private val endTimeStr: String) {

    private val startTime = parseTime(startTimeStr)
    private val endTime = parseTime(endTimeStr)

    private fun parseTime(timeStr: String): Triple<Int, Int, Int> {
        val (hours, minutes, seconds) = timeStr.split(":").map { it.toInt() }
        return Triple(hours, minutes, seconds)
    }

    private fun toSeconds(time: Triple<Int, Int, Int>): Int {
        return time.first * 3600 + time.second * 60 + time.third
    }

    fun isWithinRange(timeStr: String): Boolean {
        val time = parseTime(timeStr)
        val currentTimeInSeconds = toSeconds(time)
        val startTimeInSeconds = toSeconds(startTime)
        val endTimeInSeconds = toSeconds(endTime)

        return if (endTimeInSeconds < startTimeInSeconds) {
            currentTimeInSeconds >= startTimeInSeconds || currentTimeInSeconds < endTimeInSeconds
        } else {
            currentTimeInSeconds in (startTimeInSeconds until endTimeInSeconds)
        }
    }

    fun millisUntilEnd(currentTimeStr: String): Long {
        val currentTime = parseTime(currentTimeStr)

        val currentTimeInSeconds = toSeconds(currentTime)
        val startTimeInSeconds = toSeconds(startTime)
        val endTimeInSeconds = toSeconds(endTime)

        val durationInSeconds: Int

        if (endTimeInSeconds < startTimeInSeconds) {
            durationInSeconds = if (currentTimeInSeconds >= startTimeInSeconds) {
                (86400 - currentTimeInSeconds) + endTimeInSeconds // 86400 seconds in a day
            } else {
                endTimeInSeconds - currentTimeInSeconds
            }
        } else {
            durationInSeconds = endTimeInSeconds - currentTimeInSeconds
        }

        val durationInMillis = durationInSeconds * 1000L
        return durationInMillis
    }

    override fun toString(): String {
        return "TimeRange(startTime=$startTimeStr, endTime=$endTimeStr)"
    }
}
