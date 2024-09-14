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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.example.sleep_cycle.data.model.Vertice
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Color

interface Vertices {
     var start: Int;
     var end: Int;
}



@Composable
fun Clock(vertices: List<Vertice>?) {
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

    // Pulse animation for slices
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        drawClockFace(size = size)

        // Draw the slices based on indices
        if (vertices != null) {
            vertices.forEach { vertice ->
                val startAngle = vertice.start.toFloat() - 90f // Offset to start at the top
                val sweepAngle = (vertice.end - vertice.start).toFloat()

                // Check if the clock hand is within the vertice's range
                val isHandWithinVertice = clockHandLocation >= vertice.start && clockHandLocation <= vertice.end

                // Apply pulsating color if the clock hand is on top of the vertice
                val sliceColor = if (isHandWithinVertice) {
                    Color(0xFFADDFBF).copy(alpha = pulseAlpha) // Pulsate color
                } else {
                    Color(0xFFADDFBF) // Normal color
                }

                drawSlice(size = size, startAngle = startAngle, sweepAngle = sweepAngle, color = sliceColor)
            }
        }

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

fun DrawScope.drawSlice(size: Size, startAngle: Float, sweepAngle: Float, color: Color) {
    // Draw an arc to represent the slice
    val radius = size.minDimension / 2 * 0.75f
    val center = Offset(size.width / 2, size.height / 2)
    drawArc(
        color = color, // Use the passed color, which will pulsate if within range
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = true, // This creates the "pizza slice" effect
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2)
    )
}
