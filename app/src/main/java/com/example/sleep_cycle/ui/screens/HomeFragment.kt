package com.example.sleep_cycle.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.ui.components.SleepCycleList
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.livedata.observeAsState
import com.example.sleep_cycle.ui.components.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: SleepCycleViewModel) {
    // Observe sleepCycles and activeSleepCycle from the ViewModel
    viewModel.getAllSleepCycles()
    val sleepCycles by viewModel.sleepCycles.observeAsState(initial = emptyList()) // Observe list of cycles
    val activeSleepCycle by viewModel.activeSleepCycle.observeAsState()

    // Loading state
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(sleepCycles) {
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sleep Cycles",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else if (activeSleepCycle != null && activeSleepCycle!!.sleepTimes.isNotEmpty()) {
                Clock(vertices = activeSleepCycle!!.getSleepTimeVertices())
            }else if (activeSleepCycle == null){
                Text(
                    text = "No active sleep cycle found.",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    modifier = Modifier.padding(8.dp)
                )
            }
            else {
                Text(
                    text = "No sleep times found.",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    modifier = Modifier.padding(8.dp)
                )

                Button(
                    onClick = { activeSleepCycle?.let { viewModel.setSleepCycle(sleepCycle = it) }
                              navController.navigate("detailsScreen")
                              },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Add Sleep Times")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("newCycleScreen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Create New Sleep Cycle")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SleepCycle List
            SleepCycleList(
                sleepCycles = sleepCycles,
                navController = navController,
                sleepCycleViewModel = viewModel,
                limit = 3
            )
        }
    }
}
