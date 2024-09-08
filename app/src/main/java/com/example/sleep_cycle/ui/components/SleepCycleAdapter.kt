// SleepCycleList.kt
package com.example.sleep_cycle.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.helper.Time

@Composable
fun SleepCycleList(
    sleepCycles: List<SleepCycle>,
    navController: NavController,
    sleepCycleViewModel: SleepCycleViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        sleepCycles.forEach { sleepCycle ->
            SleepCycleItem(
                sleepCycle = sleepCycle,
                onClick = {
                    sleepCycleViewModel.setSleepCycle(sleepCycle)

                    navController.navigate("detailsScreen")
                }
            )
        }
    }
}

@Composable
fun SleepCycleItem(
    sleepCycle: SleepCycle,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = sleepCycle.name,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Sleep time ${Time.minutesToHHMM(sleepCycle.totalSleepTime())}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
