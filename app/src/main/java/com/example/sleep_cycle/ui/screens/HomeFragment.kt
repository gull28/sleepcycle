package com.example.sleep_cycle.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sleep_cycle.data.repository.Preference
import com.example.sleep_cycle.data.viewmodels.PreferenceViewModel
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.helper.Time
import com.example.sleep_cycle.ui.components.Clock
import com.example.sleep_cycle.ui.components.SleepCycleList
import com.example.sleep_cycle.ui.theme.AppColors

@Composable
fun HomeScreen(navController: NavController, viewModel: SleepCycleViewModel, preferences: PreferenceViewModel) {
    viewModel.getAllSleepCycles()
    val sleepCycles by viewModel.sleepCycles.observeAsState(initial = emptyList()) // Observe list of cycles
    val activeSleepCycle by viewModel.activeSleepCycle.observeAsState()
    val mode = preferences.modeFlow.collectAsState(initial = true)

    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(sleepCycles) {
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppColors.Background)
            .padding(horizontal = 8.dp)
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
                    navController.navigate("")
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

            Spacer(modifier = Modifier.height(16.dp))

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
                        .padding(bottom = 10.dp)
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


            SleepCycleList(
                sleepCycles = sleepCycles,
                navController = navController,
                sleepCycleViewModel = viewModel,
            )
        }
    }
}
