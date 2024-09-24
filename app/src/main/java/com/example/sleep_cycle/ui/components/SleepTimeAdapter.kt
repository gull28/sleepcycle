package com.example.sleep_cycle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.sleep_cycle.ui.theme.AppColors
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon


@Composable
fun SleepTimeList(
    sleepTimes: List<SleepTime>,
    onEditClicked: (position: Int, sleepTime: SleepTime) -> Unit,
    onRemoveClicked: (Int, SleepTime) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 24.dp)
    ) {
        itemsIndexed(sleepTimes) { index, sleepTime ->
            SleepTimeItem(
                sleepTime = sleepTime,
                onEditClicked = { onEditClicked(index, sleepTime) },
                onRemoveClicked = { onRemoveClicked(index, sleepTime) }
            )
            Spacer(modifier = Modifier.height(12.dp)) // Space between items
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
        colors = CardDefaults.cardColors(containerColor = AppColors.Accent),
        shape = RoundedCornerShape(13.dp),
        elevation = CardDefaults.cardElevation(4.dp)
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
                    modifier = Modifier.padding(bottom = 4.dp),
                    text = sleepTime.name,
                    color = AppColors.Slate,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Start Time: ${sleepTime.startTime}",
                    fontSize = 14.sp,
                    color = AppColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Duration: ${Time.minutesToHHMM(sleepTime.duration)}",
                    fontSize = 14.sp,
                    color = AppColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

            }

            Row(
                modifier = Modifier.align(Alignment.CenterVertically)

            ) {
                IconButton(
                    onClick = onEditClicked,
                    modifier = Modifier.size(24.dp),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = AppColors.Slate)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Sleep Time",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))


                IconButton(
                    onClick = onRemoveClicked,
                    modifier = Modifier.size(24.dp),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = AppColors.Slate)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove Sleep Time",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
