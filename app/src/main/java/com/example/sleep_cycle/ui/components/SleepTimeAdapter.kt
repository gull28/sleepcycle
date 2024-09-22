package com.example.sleep_cycle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sleep_cycle.data.model.SleepTime
import com.example.sleep_cycle.helper.Time

@Composable
fun SleepTimeList(
    sleepTimes: List<SleepTime>,
    onEditClicked: (position: Int, sleepTime: SleepTime) -> Unit,
    onRemoveClicked: (Int, SleepTime) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 24.dp)
    ) {
        sleepTimes.forEachIndexed { index, sleepTime ->
            SleepTimeItem(
                sleepTime = sleepTime,
                onEditClicked = { onEditClicked(index, sleepTime) },
                onRemoveClicked = { onRemoveClicked(index, sleepTime) }
            )
            Spacer(modifier = Modifier.height(12.dp)) // Added space between items
        }
    }
}

@Composable
fun SleepTimeItem(
    sleepTime: SleepTime,
    onEditClicked: () -> Unit,
    onRemoveClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        shape = RoundedCornerShape(8.dp), // Added rounded corners
        elevation = CardDefaults.cardElevation(4.dp) // Added elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "Start Time: ${sleepTime.startTime}",
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    text = "Duration: ${Time.minutesToHHMM(sleepTime.duration)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Name: ${sleepTime.name}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Row(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                IconButton(
                    onClick = onEditClicked,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Blue)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Sleep Time")
                }
                IconButton(
                    onClick = onRemoveClicked,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Red)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove Sleep Time")
                }
            }
        }
    }
}
