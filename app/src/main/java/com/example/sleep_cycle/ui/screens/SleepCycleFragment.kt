package com.example.sleep_cycle.ui.screens

import TimeInputDialog
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sleep_cycle.data.model.SleepTime
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.ui.components.SleepTimeList
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import com.example.sleep_cycle.data.repository.SleepTimeRepository

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun SleepCycleScreen(navController: NavController, viewModel: SleepCycleViewModel) {
    val context = LocalContext.current

    val sleepTimesState = viewModel.sleepTimes.observeAsState()
    val sleepTimes = sleepTimesState.value?.toMutableList() ?: mutableListOf() // Convert to a mutable list

    val sleepCycle by viewModel.sleepCycle.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()

    val showDialog = remember { mutableStateOf(false) }
    val selectedSleepTime = remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Cycle Name: ${sleepCycle?.name ?: "Unknown"}")

        // Use the mutable version of sleepTimes for list operations
        SleepTimeList(
            sleepTimes = sleepTimes,
            onEditClicked = { position, sleepTime ->
                showDialog.value = true
                selectedSleepTime.value = position
            },
            onRemoveClicked = { position: Int ->
                sleepTimes.removeAt(position)
                viewModel.updateSleepTime(position = position, sleepTimes[position]) // Update ViewModel with the modified list
            }
        )

        Button(onClick = { showDialog.value = true }) {
            Text("Add Time")
        }

        Row(modifier = Modifier.align(Alignment.End)) {
            
            Button(
                colors = ButtonDefaults.buttonColors(Color.Red),
                modifier = Modifier.padding(horizontal = 10.dp),
                onClick = {
                    sleepCycle?.id?.let { viewModel.deleteSleepCycle(id = it) }

                    navController.navigate("home")
                }
            ){
                Text(
                    text = "Delete"
                )
            }

            Button(
                onClick = {
                    navController.navigateUp()
            }) {
                Text("Done")
            }
        }

        if (showDialog.value) {
            val currentSleepTime = selectedSleepTime.value?.let { sleepTimes.getOrNull(it) }
            TimeInputDialog(
                sleepTime = currentSleepTime,
                onSave = { sleepTime ->
                    handleSaveSleepTime(sleepTime, viewModel, context, selectedSleepTime.value, navController)
                    selectedSleepTime.value = null
                },
                setShowDialog = {
                    showDialog.value = it
                },
                onDismiss = {
                    selectedSleepTime.value = null
                    showDialog.value = false
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun handleSaveSleepTime(
    sleepTime: SleepTime,
    viewModel: SleepCycleViewModel,
    context: Context,
    selectedSleepTime: Int?,
    navController: NavController
) {
    // Check if editing
    val repository = SleepTimeRepository(context)

    if (selectedSleepTime != null) {
        // Edit route]
        val res = repository.updateSleepTime(sleepTime)

        navController.navigate("home")
    } else {
        // Add route
        val overlappingTime = viewModel.sleepTimes.value?.find {
            it.isTimeInTimeFrame(sleepTime.startTime, sleepTime.calculateEndTime())
        }
        val sameStartTime = viewModel.sleepTimes.value?.find { it.startTime == sleepTime.startTime }

        if (overlappingTime != null) {
            Toast.makeText(context, "This time overlaps with an existing SleepTime.", Toast.LENGTH_SHORT).show()
        } else if (sameStartTime == null) {

            val scheduleId = viewModel.sleepCycle.value?.id
            if (scheduleId != null) {
                repository.addSleepTime(sleepTime, scheduleId)
            }
            navController.navigate("home")
        } else {
            Toast.makeText(context, "A SleepTime with the same start time already exists.", Toast.LENGTH_SHORT).show()
        }
    }
}

