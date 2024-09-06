// SleepTimeList.kt
package com.example.sleep_cycle.ui.components

import android.util.Log
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sleep_cycle.data.model.SleepTime

@Composable
fun SleepTimeList(
    sleepTimes: List<SleepTime>,
    onEditClicked: (position: Int, sleepTime: SleepTime) -> Unit,
    onRemoveClicked: (position: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 80.dp)
    ) {
        sleepTimes.forEachIndexed { index, sleepTime ->
            SleepTimeItem(
                sleepTime = sleepTime,
                onEditClicked = { onEditClicked(index, sleepTime) },
                onRemoveClicked = { onRemoveClicked(index) }
            )
        }
    }
}


@Composable
fun SleepTimeItem(
    sleepTime: SleepTime,
    onEditClicked: () -> Unit,
    onRemoveClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Start Time: ${sleepTime.startTime}", fontSize = 16.sp)
            Text(text = "Duration: ${sleepTime.duration}", fontSize = 14.sp)
            Text(text = "Name: ${sleepTime.name}", fontSize = 14.sp)
        }

        Row {
            IconButton(onClick = onEditClicked) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Sleep Time")
            }
            IconButton(onClick = onRemoveClicked) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove Sleep Time")
            }
        }
    }
}
