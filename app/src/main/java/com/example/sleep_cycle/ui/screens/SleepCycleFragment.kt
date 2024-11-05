package com.example.sleep_cycle.ui.screens

import TimeInputDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sleep_cycle.data.models.SleepTime
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.ui.components.SleepTimeList
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.sleep_cycle.ui.components.Clock
import com.example.sleep_cycle.ui.theme.AppColors

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun SleepCycleScreen(navController: NavController, viewModel: SleepCycleViewModel) {
    val context = LocalContext.current

    val sleepTimesState = viewModel.sleepTimes.observeAsState()
    val sleepTimes = sleepTimesState.value?.toMutableList() ?: mutableListOf()

    val sleepCycle by viewModel.sleepCycle.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()

    val showDialog = remember { mutableStateOf(false) }
    val editedSleepTime = remember { mutableStateOf<Int?>(null) }
    val selectedSleepTime = remember { mutableStateOf<Int?>(null)}

    val selectedVertice = selectedSleepTime.value?.let { index ->
        sleepTimes.getOrNull(index)?.getVertice()
    }


    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppColors.Background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Clock(vertices = sleepTimes.map {
                it.getVertice() },
                selectedVertice = selectedVertice,
            )

            Text(
                text = sleepCycle?.name ?: "Unknown",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Slate,
                modifier = Modifier.align(Alignment.Start)
            )

            SleepTimeList(
                sleepTimes = sleepTimes,
                onEditClicked = { position, sleepTime ->
                    showDialog.value = true
                    editedSleepTime.value = position
                },
                selectedSleepTime = selectedSleepTime.value,
                setSelectedSleepTime = { position: Int, _: SleepTime ->
                    if(position == selectedSleepTime.value){
                        selectedSleepTime.value = null
                        return@SleepTimeList
                    }
                    selectedSleepTime.value = position
                },
                onRemoveClicked = { _, sleepTime ->
                    sleepTime.id?.let { viewModel.removeSleepTime(it) }

                    viewModel.resetNotifAction()
                }
            )
        }

        Row(
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Button(
                onClick = {
                    sleepCycle?.let { viewModel.deleteSleepCycle(it) }

                    navController.navigateUp()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                contentPadding = PaddingValues(vertical = 18.dp),
                shape = RoundedCornerShape(13.dp),
                modifier = Modifier
                    .padding(end = 10.dp)
                    .fillMaxWidth(0.5f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = AppColors.Slate
                    )
                    Text(
                        letterSpacing = 1.1.sp,
                        fontWeight = FontWeight(385),
                        fontSize = 16.sp,
                        text = "Delete"
                    )
                }
            }

            Button(
                onClick = { showDialog.value = true },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                contentPadding = PaddingValues(vertical = 18.dp),
                shape = RoundedCornerShape(13.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Done",
                        tint = AppColors.Slate
                    )
                    Text(
                        letterSpacing = 1.1.sp,
                        fontWeight = FontWeight(385),
                        fontSize = 16.sp,
                        text = "Add time"
                    )
                }
            }
        }

        if (showDialog.value) {
            val currentSleepTime = editedSleepTime.value?.let { sleepTimes.getOrNull(it) }
            TimeInputDialog(
                sleepTime = currentSleepTime,
                onSave = { sleepTime ->
                    handleSaveSleepTime(sleepTime, viewModel, context, editedSleepTime.value, navController)
                    editedSleepTime.value = null
                    viewModel.resetNotifAction()
                },
                setShowDialog = {
                    showDialog.value = it
                },
                onDismiss = {
                    editedSleepTime.value = null
                    showDialog.value = false
                },
                sleepCycleViewModel = viewModel
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
    if (selectedSleepTime != null) {
        // Edit route
        val res = viewModel.updateSleepTime(sleepTime)

        viewModel.getAllSleepCycles()
    } else {
        // Add route
        val filteredSleepTimes = viewModel.sleepTimes.value?.filter {
            it.id != sleepTime.id;
        }

        val overlappingTime = filteredSleepTimes?.find {
            it.isTimeInTimeFrame(sleepTime.startTime, sleepTime.calculateEndTime())
        }
        val sameStartTime = filteredSleepTimes?.find { it.startTime == sleepTime.startTime }

        if (overlappingTime != null) {
            Toast.makeText(context, "This time overlaps with an existing SleepTime.", Toast.LENGTH_SHORT).show()
        } else if (sameStartTime == null) {

            val scheduleId = viewModel.sleepCycle.value?.id

            if (scheduleId != null) {
                viewModel.addSleepTime(sleepTime, scheduleId)

                viewModel.getAllSleepCycles()
            }
        } else {
            Toast.makeText(context, "A SleepTime with the same start time already exists.", Toast.LENGTH_SHORT).show()
        }
    }
}


