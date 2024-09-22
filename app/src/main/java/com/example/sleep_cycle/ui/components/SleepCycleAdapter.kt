// SleepCycleList.kt
package com.example.sleep_cycle.ui.components

import android.content.Intent
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.sleep_cycle.ForegroundService
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.helper.Time
import com.example.sleep_cycle.ui.theme.AppColors


@Composable
fun SleepCycleList(
    sleepCycles: List<SleepCycle>,
    navController: NavController,
    sleepCycleViewModel: SleepCycleViewModel,
    limit: Int,
) {
    val listCount = remember { mutableIntStateOf(limit) }

    val activeSleepCycle by sleepCycleViewModel.activeSleepCycle.observeAsState()

    var mutatedSleepCycles = sleepCycles
    if (sleepCycles.size >= listCount.intValue) {
        mutatedSleepCycles = sleepCycles.slice(indices = IntRange(0, listCount.intValue - 1))
    }

    val showShowMoreButton = sleepCycles.size > listCount.intValue

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .animateContentSize()
            .heightIn(max = if (showShowMoreButton) Dp.Unspecified else 200.dp)
    ) {
        mutatedSleepCycles.forEach { sleepCycle ->
            SleepCycleItem(
                sleepCycle = sleepCycle,
                isActive = activeSleepCycle?.id == sleepCycle.id,
                onClick = {
                    sleepCycleViewModel.setSleepCycle(sleepCycle)
                    navController.navigate("detailsScreen")
                },
                onToggleActive = { cycle ->
                    val isCurrentlyActive = activeSleepCycle?.id == cycle.id

                    if (isCurrentlyActive) {
                        sleepCycleViewModel.toggleActive(cycle.id!!)
                        sleepCycleViewModel.setActiveSleepCycle(null)
                    } else {
                        sleepCycleViewModel.toggleActive(cycle.id!!)
                        sleepCycleViewModel.setActiveSleepCycle(cycle)
                    }
                }
            )
        }

        if (showShowMoreButton) {
            Button(
                onClick = {
                    listCount.intValue += 5
                }
            ) {
                Text(text = "Show more")
            }
        }
    }
}


@Composable
fun SleepCycleItem(
    sleepCycle: SleepCycle,
    isActive: Boolean,
    onClick: () -> Unit,
    onToggleActive: (SleepCycle) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        color = AppColors.Accent,
        shape = RoundedCornerShape(13.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = sleepCycle.name,
                    color = AppColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Sleep time ${Time.minutesToHHMM(sleepCycle.totalSleepTime())}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                    color = AppColors.TextSecondary
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Switch(
                checked = isActive,
                onCheckedChange = {
                    sleepCycle.id?.let {
                        onToggleActive(sleepCycle)
                    }
                },
                colors = SwitchDefaults.colors(checkedThumbColor = AppColors.Primary, checkedTrackColor = AppColors.Slate)

            )
        }
    }
}
