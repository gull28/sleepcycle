package com.example.sleep_cycle.ui.screens

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.font.FontWeight
import com.example.sleep_cycle.helper.Time
import com.example.sleep_cycle.ui.components.Clock
import com.example.sleep_cycle.ui.theme.AppColors

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

    val nextCycle = activeSleepCycle?.getNextSleepTime()

    Log.d("nextCyc", nextCycle.toString())
    val timeUntilNextSleepTime = nextCycle?.startTime?.let {
        Time.stringToDateObj(
            it
        )
    }?.let { Time.getTimeUntil(it) }

    Log.d("timeUntilNextSleepTime", timeUntilNextSleepTime.toString())
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppColors.Background)
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Clock/Timer",
                    color = AppColors.Slate,
                    fontWeight = FontWeight(400),
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = true,
                    onCheckedChange = {
                        // Handle switch state change
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AppColors.Primary,
                        checkedTrackColor = AppColors.Slate
                    )
                )
            }



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
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.ClockFace),
                    contentPadding = PaddingValues(vertical = 18.dp),
                    shape = RoundedCornerShape(13.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp) // Space between text and symbol
                    ) {
                        Text(
                            letterSpacing = 1.1.sp,
                            fontWeight = FontWeight(400),
                            fontSize = 16.sp,
                            text = "Add sleep times"
                        )
                        Text(
                            text = "+",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White // Change color as needed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { navController.navigate("newCycleScreen") },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                    contentPadding = PaddingValues(vertical = 18.dp),
                    shape = RoundedCornerShape(13.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 16.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp) // Space between text and symbol
                    ) {
                        Text(
                            text = "+ ",
                            fontWeight = FontWeight.W300,
                            fontSize = 24.sp,
                            color = AppColors.Slate // Change color as needed
                        )
                        Text(
                            letterSpacing = 1.1.sp,
                            fontWeight = FontWeight(385),
                            fontSize = 16.sp,
                            text = "Create New Sleep Cycle ")

                    }
                }
            }

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
