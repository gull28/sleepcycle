package com.example.sleep_cycle.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.ui.components.SleepCycleList
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.livedata.observeAsState
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.ui.components.Clock

@Composable
fun HomeScreen(navController: NavController, viewModel: SleepCycleViewModel) {
    // Observe sleepCycles and activeSleepCycle from the ViewModel
    viewModel.getAllSleepCycles()
    val sleepCycles by viewModel.sleepCycles.observeAsState(initial = emptyList()) // Observe list of cycles
    val activeSleepCycle by viewModel.activeSleepCycle.observeAsState() // Observe active sleep cycle

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(
                onClick = { navController.navigate("newCycleScreen") },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text("Create New Cycle")
            }

            Clock(vertices = activeSleepCycle?.getSleepTimeVertices())

            Spacer(modifier = Modifier.height(16.dp))

            SleepCycleList(
                sleepCycles = sleepCycles,
                navController = navController,
                sleepCycleViewModel = viewModel,
                limit = 3
            )
        }
    }
}
