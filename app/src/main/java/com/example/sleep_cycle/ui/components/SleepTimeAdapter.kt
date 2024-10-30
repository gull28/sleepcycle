package com.example.sleep_cycle.ui.components

import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sleep_cycle.data.models.SleepTime
import com.example.sleep_cycle.helpers.Time
import com.example.sleep_cycle.ui.theme.AppColors
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color


@Composable
fun SleepTimeList(
    sleepTimes: List<SleepTime>,
    selectedSleepTime: Int?, // position for inclusion
    setSelectedSleepTime: (position: Int, sleepTime: SleepTime) -> Unit,
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
                onClick = { setSelectedSleepTime(index, sleepTime)  },
                onEditClicked = { onEditClicked(index, sleepTime) },
                onRemoveClicked = { onRemoveClicked(index, sleepTime) },
                isSelected = index == selectedSleepTime // Check if the current item is selected
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}


@Composable
fun SleepTimeItem(
    sleepTime: SleepTime,
    onEditClicked: () -> Unit,
    onRemoveClicked: () -> Unit,
    onClick: () -> Unit,
    isSelected: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) AppColors.Primary else Color.Transparent,
                shape = RoundedCornerShape(13.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = AppColors.Accent),
        shape = RoundedCornerShape(13.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick,
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
                    text = "Start Time: ${sleepTime.startTime.substring(0, 5)}",
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
