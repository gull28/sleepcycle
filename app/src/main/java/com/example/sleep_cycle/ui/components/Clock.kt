package com.example.sleep_cycle.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Clock() {
    var currentTime by remember { mutableStateOf(Calendar.getInstance()) }

    // Update time every second
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000L)
            currentTime = Calendar.getInstance()
        }
    }

    val calendar = currentTime
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val degreesPerHour = 360.0 / 24
    val degreesPerMinute = degreesPerHour / 60

    val clockHandLocation: Double = (degreesPerHour * hour) + (degreesPerMinute * minute)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        drawClockFace(size = size)
        drawClockHand(size = size, angle = clockHandLocation)
    }
}

fun DrawScope.drawClockFace(size: Size) {
    // Draw clock face
    drawCircle(
        color = Color.Gray,
        radius = size.minDimension / 2 * 0.75f,
        center = Offset(size.width / 2, size.height / 2)
    )
}

fun DrawScope.drawClockHand(size: Size, angle: Double) {
    val radius = size.minDimension / 2 * 0.8f
    val center = Offset(size.width / 2, size.height / 2)

    val handLength = radius * 0.8f
    val handEnd = Offset(
        center.x + (handLength * cos((angle - 90) * PI / 180)).toFloat(),
        center.y + (handLength * sin((angle - 90) * PI / 180)).toFloat()
    )

    drawLine(
        color = Color.Black,
        start = center,
        end = handEnd,
        strokeWidth = 5.dp.toPx()
    )
}
