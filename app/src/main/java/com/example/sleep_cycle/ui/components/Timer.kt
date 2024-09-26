package com.example.sleep_cycle.ui.components

import TimeRange
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.helper.Time
import kotlinx.coroutines.delay
import com.example.sleep_cycle.ui.theme.AppColors
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.Canvas
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.ui.graphics.drawscope.Stroke


interface TimerData {
    var text: String
    var timer: Long
    var duration: Long; // ms
    var displayRingDown: Boolean;
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Timer(cycle: SleepCycle) {
    var timeData by remember { mutableStateOf(getTimer(cycle)) }
    var remainingTime by remember { mutableLongStateOf(timeData.timer) }
    val currentTimeText by rememberUpdatedState(timeData.text)

    LaunchedEffect(cycle) {
        timeData = getTimer(cycle)
        remainingTime = timeData.timer
    }

    LaunchedEffect(remainingTime) {
        if (remainingTime > 0) {
            delay(1000L)
            remainingTime -= 1000L
        } else {
            timeData = getTimer(cycle)
            remainingTime = timeData.timer
        }
    }

    val progressPercent = if (timeData.timer > 0 && timeData.displayRingDown) {
        (remainingTime / timeData.duration.toFloat()) * 100
    } else {
        100f
    }

    val totalMillis = remainingTime
    val hours = (totalMillis / (1000 * 60 * 60)) % 24
    val minutes = (totalMillis / (1000 * 60)) % 60
    val seconds = (totalMillis / 1000) % 60

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(330.dp)
    ) {
        CircularProgressBar(progressPercent)

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentTimeText,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 20.dp),
                color = AppColors.Slate.copy(alpha = 0.75f),
                letterSpacing = 1.05.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TimeDisplay(hours)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = ":", fontSize = 42.sp, color = AppColors.Slate)
                Spacer(modifier = Modifier.width(4.dp))
                TimeDisplay(minutes)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = ":", fontSize = 42.sp, color = AppColors.Slate)
                Spacer(modifier = Modifier.width(4.dp))
                TimeDisplay(seconds)
            }
        }
    }
}

@Composable
fun TimeDisplay(value: Long) {
    Text(text = "%02d".format(value), fontSize = 42.sp, color = AppColors.Slate)
}


@Composable
fun CircularProgressBar(progressPercent: Float) {
    val progress = progressPercent / 100f
    val strokeWidth = 6.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(330.dp)
    ) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.size(330.dp),
            color = AppColors.Slate.copy(alpha = 0.35f),
            strokeWidth = strokeWidth,
            trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
        )


        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(330.dp),
            color = AppColors.Primary,
            strokeWidth = strokeWidth,
            trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
        )
    }
}



@RequiresApi(Build.VERSION_CODES.O)
fun getTimer(cycle: SleepCycle): TimerData {
    val currentTime = LocalTime.now()
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    val activeSleepTime = cycle.sleepTimes.find { sleepTime ->
        sleepTime.isTimeInTimeFrame(currentTime.format(formatter), currentTime.format(formatter))
    }

    Log.d("activsSleepTime", activeSleepTime.toString())

    activeSleepTime?.let {
        return object : TimerData {
            override var text = "Active ${it.name}"
            override var timer = TimeRange(
                it.startTime,
                it.calculateEndTime()
            ).millisUntilEnd(currentTime.format(formatter))
            override var duration = it.duration.toLong() * 60 * 1000;
            override var displayRingDown = true;
        }
    }

    val nextSleepTime = cycle.getNextSleepTime()

    nextSleepTime?.let { it ->
        return object : TimerData {
            override var text = "Upcoming ${it.name}"
            override var timer = Time.getTimeUntil(Time.stringToDateObj(it.startTime))
            override var duration = 0L;
            override var displayRingDown = false;
        }
    }

    return object : TimerData {
        override var text = "No upcoming sleep cycle";
        override var timer = 1000L;
        override var duration = 0L;
        override  var displayRingDown = false;
    }
}
