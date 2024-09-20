package com.example.sleep_cycle.ui.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
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

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val animatedSweepAngles = vertices?.mapIndexed { index, vertice ->
        val targetSweepAngle = calculateClockwiseSweepAngle(vertice.start, vertice.end)
        animateFloatAsState(
            targetValue = targetSweepAngle,
            animationSpec = tween(durationMillis = 1000 + (index * 200), easing = FastOutSlowInEasing),
            label = ""
        ).value
    } ?: emptyList()

    Canvas(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .aspectRatio(1f)
    ) {
        drawClockFace(size = size)

        vertices?.forEachIndexed { index, vertice ->
            val startAngle = vertice.start.toFloat() - 90f
            val sweepAngle = animatedSweepAngles[index]

            val isHandWithinVertice = clockHandLocation >= vertice.start && clockHandLocation <= vertice.end

            val sliceColor = if (isHandWithinVertice) {
                Color(0xFFADDFBF).copy(alpha = pulseAlpha) // Pulsate color
            } else {
                Color(0xFFADDFBF) // Normal color
            }

            drawSlice(size = size, startAngle = startAngle, sweepAngle = sweepAngle, color = sliceColor)
        }

        drawClockHand(size = size, angle = clockHandLocation)
    }
}

fun DrawScope.drawClockFace(size: Size) {
    drawCircle(
        color = Color.Gray,
        radius = size.minDimension / 2 * 0.85f,
        center = Offset(size.width / 2, size.height / 2)
    )
}

fun calculateClockwiseSweepAngle(start: Int, end: Int): Float {
    return if (end >= start) {
        (end - start).toFloat()
    } else {
        (360 - start + end).toFloat()
    }
}

fun DrawScope.drawClockHand(size: Size, angle: Double) {
    val radius = size.minDimension / 2 * 0.9f
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
        strokeWidth = 3.2.dp.toPx()
    )
}

fun DrawScope.drawSlice(size: Size, startAngle: Float, sweepAngle: Float, color: Color) {
    val radius = size.minDimension / 2 * 0.85f
    val center = Offset(size.width / 2, size.height / 2)
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = true,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2)
    )
}
