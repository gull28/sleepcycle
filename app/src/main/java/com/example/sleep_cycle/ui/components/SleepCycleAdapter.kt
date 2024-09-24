// SleepCycleList.kt
package com.example.sleep_cycle.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.helper.Time
import com.example.sleep_cycle.ui.theme.AppColors


@Composable
fun SleepCycleList(
    sleepCycles: List<SleepCycle>,
    navController: NavController,
    sleepCycleViewModel: SleepCycleViewModel
) {
    val activeSleepCycle by sleepCycleViewModel.activeSleepCycle.observeAsState()

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 24.dp)
    ) {
        items(sleepCycles) { sleepCycle ->
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
            .padding(vertical = 5.dp)
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
