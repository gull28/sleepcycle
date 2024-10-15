package com.example.sleep_cycle.ui.screens

import TimeRange
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sleep_cycle.data.viewmodels.PreferenceViewModel
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.ui.components.Clock
import com.example.sleep_cycle.ui.components.SleepCycleList
import com.example.sleep_cycle.ui.components.Timer
import com.example.sleep_cycle.ui.theme.AppColors

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController, viewModel: SleepCycleViewModel, preferences: PreferenceViewModel) {
    viewModel.getAllSleepCycles()
    val sleepCycles by viewModel.sleepCycles.observeAsState(initial = emptyList())
    val activeSleepCycle by viewModel.activeSleepCycle.observeAsState()
    val mode = preferences.modeFlow.collectAsState(initial = true)

    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(sleepCycles) {
        isLoading = false
    }

    val hasSeenOverlay by preferences.batteryInfoShownFlow.collectAsState(initial = false)
    val showOverlay = !hasSeenOverlay

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppColors.Background)
            .padding(horizontal = 8.dp),
        color = AppColors.Background

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.End
            ) {

                IconButton(onClick = {
                    navController.navigate("settingsScreen")
                },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = AppColors.Primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Edit Sleep Time",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else if (activeSleepCycle != null && activeSleepCycle!!.sleepTimes.isNotEmpty()) {
                if(mode.value){
                    Timer(cycle = activeSleepCycle!!)
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Clock(vertices = activeSleepCycle!!.getSleepTimeVertices(), selectedVertice = null)
                }
            } else if (activeSleepCycle == null){
                Text(
                    text = "No active sleep cycle found.",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                Text(
                    text = "No sleep times found.",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .padding(bottom = 48.dp)
                )

                Button(
                    onClick = { activeSleepCycle?.let { viewModel.setSleepCycle(sleepCycle = it) }
                        navController.navigate("sleepCycleScreen")
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
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            letterSpacing = 1.1.sp,
                            fontWeight = FontWeight(400),
                            fontSize = 16.sp,
                            text = "Add sleep times"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { navController.navigate("newCycleScreen") },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                    contentPadding = PaddingValues(vertical = 18.dp),
                    shape = RoundedCornerShape(13.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "+ ",
                            fontWeight = FontWeight.W300,
                            fontSize = 24.sp,
                            color = AppColors.Slate
                        )
                        Text(
                            letterSpacing = 1.1.sp,
                            fontWeight = FontWeight(385),
                            fontSize = 16.sp,
                            text = "Create New Sleep Cycle"
                        )
                    }
                }
            }

            Row (
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth().padding(
                    top = 10.dp,
                    bottom = 4.dp,
                )
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                    ),
                    onClick = {
                    navController.navigate("sleepCycleListScreen")
                }) {
                    Text(fontSize = 16.sp, text = "View all", color = AppColors.Primary)
                }
            }
            SleepCycleList(
                sleepCycles = sleepCycles,
                navController = navController,
                sleepCycleViewModel = viewModel,
            )
        }

        if (showOverlay) {
            OverlayMessage(
                onDismiss = {
                    preferences.saveBatteryInfoShownFlow(true)
                }
            )
        }
    }
}

@Composable
fun OverlayMessage(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background.copy(alpha = 0.33f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .background(
                    AppColors.Background.copy(alpha = 0.95f),
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .fillMaxHeight(0.30f),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Important Information",
                style = MaterialTheme.typography.headlineMedium,
                color = AppColors.Slate,
                letterSpacing = 1.05.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "For the best experience, please ensure that battery optimizations are disabled for this app. You can do this in your device settings.",
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.TextSecondary,
                fontSize = 18.sp,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(15.dp),
            ) {
                Text(text = "Got it!")
            }
        }
    }
}
