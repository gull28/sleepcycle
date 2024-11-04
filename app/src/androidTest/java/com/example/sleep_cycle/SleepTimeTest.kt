package com.example.sleep_cycle

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sleep_cycle.data.models.SleepTime
import com.example.sleep_cycle.helpers.Time
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class SleepTimeTest {
    private lateinit var sleepTime: SleepTime;

    @Before
    fun setup() {
        sleepTime = SleepTime(
            id = 1,
            name = "Sleep Time Test",
            duration = 360,
            startTime = "01:30",
        )
    }

    private fun formatTime(hour: Int, minute: Int): String {
        return LocalTime.of(hour, minute).format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    }

    @Test
    fun testCalculateEndTimeWithoutCrossingMidnight() {
        sleepTime.startTime = "10:00:00"
        sleepTime.duration = 90 // 1 hour 30 minutes
        assertEquals("11:30:00", sleepTime.calculateEndTime())
    }

    @Test
    fun testCalculateEndTimeWithCrossingMidnight() {
        sleepTime.startTime = "22:00:00"
        sleepTime.duration = 360
        assertEquals("04:00:00", sleepTime.calculateEndTime())
    }

    @Test
    fun testVerticeWithoutCrossingMidnight() {
        sleepTime.startTime = "12:00:00"
        sleepTime.duration = 120

        val vertice = sleepTime.getVertice()

        assertEquals(180, vertice.start)
        assertEquals(210, vertice.end)
    }

    @Test
    fun testVerticeWithCrossingMidnight() {
        sleepTime.startTime = "21:00:00"
        sleepTime.duration = 360

        val vertice = sleepTime.getVertice()

        assertEquals(315, vertice.start)
        assertEquals(45, vertice.end)
    }

    // important below

    @Test
    fun testIsTimeInTimeFrame_SameStart() {
        sleepTime.startTime = "12:00:00"

        val start = "12:00:00"
        val end = "16:00:00"

        val isInTimeFrame = sleepTime.isTimeInTimeFrame(start, end)

        assertEquals(true, isInTimeFrame)
    }

    @Test
    fun testIsTimeInTimeFrame_SameEnd() {
        sleepTime.startTime = "12:00:00"
        sleepTime.duration = 120

        val start = "11:00:00"
        val end = "14:00:00"

        val isInTimeFrame = sleepTime.isTimeInTimeFrame(start, end)

        assertEquals(true, isInTimeFrame)
    }

    @Test
    fun testIsTimeInTimeFrame_SleepTimeCrossingMidnight() {
        sleepTime.startTime = "21:00:00"
        sleepTime.duration = 600 // 10 hours -> end = "07:00:00"

        val start = "03:00:00"
        val end = "11:00:00"

        val isInTimeFrame = sleepTime.isTimeInTimeFrame(start, end)

        assertEquals(true, isInTimeFrame)
    }

    @Test
    fun testIsTimeInTimeFrame_TestedPassedMidnight() {
        sleepTime.startTime = "02:00:00"
        sleepTime.duration = 600

        val start = "21:00:00"
        val end = "11:00:00"

        val isInTimeFrame = sleepTime.isTimeInTimeFrame(start, end)

        assertEquals(true, isInTimeFrame)
    }

    @Test
    fun testIsTimeInTimeFrame_BothCrossingMidnight() {
        sleepTime.startTime = "21:00:00"
        sleepTime.duration = 490

        val start = "23:00:00"
        val end = "09:00:00"

        val isInTimeFrame = sleepTime.isTimeInTimeFrame(start, end)

        assertEquals(true, isInTimeFrame)
    }

    // VALID
    @Test
    fun testIsTimeInTimeFrame_SleepTimeCrossingMidnightV() {
        sleepTime.startTime = "21:00:00"
        sleepTime.duration = 490

        val start = "16:00:00"
        val end = "18:00:00"

        val isInTimeFrame = sleepTime.isTimeInTimeFrame(start, end)

        assertEquals(false, isInTimeFrame)
    }

    @Test
    fun testIsTimeInTimeFrame_TestedCrossingMidnightV() {
        sleepTime.startTime = "12:00:00"
        sleepTime.duration = 420

        val start = "20:00:00"
        val end = "11:00:00"

        val isInTimeFrame = sleepTime.isTimeInTimeFrame(start, end)

        assertEquals(false, isInTimeFrame)
    }
}