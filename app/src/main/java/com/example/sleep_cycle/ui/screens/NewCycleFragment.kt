package com.example.sleep_cycle.ui.screens

import TimeInputDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.ui.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.example.sleep_cycle.data.model.SleepTime
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.ui.components.Clock
import com.example.sleep_cycle.ui.components.SleepTimeList
import com.example.sleep_cycle.ui.theme.AppColors

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewCycleFragment(navController: NavController, viewModel: SleepCycleViewModel) {
    val sleepTimes = remember { mutableStateListOf<SleepTime>() }
    val selectedSleepTime = remember { mutableStateOf<Int?>(null) }
    val name = remember { mutableStateOf("") }
    val localContext = LocalContext.current
    val sleepCycleRepository =  SleepCycleRepository(localContext)

    val vertices = sleepTimes.map { time ->
        time.getVertice()
    }

    rememberCoroutineScope()
    val showDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ){
            Clock(vertices = vertices)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                label = { Text("Name")},
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = AppColors.Slate.copy(alpha = 0.75f),
                    focusedBorderColor = AppColors.Primary,
                    focusedLabelColor = AppColors.TextPrimary,
                    unfocusedTextColor = AppColors.TextPrimary,
                    unfocusedLabelColor = AppColors.TextPrimary.copy(alpha = 0.75f),
                    focusedTextColor = AppColors.TextPrimary,
                ),
                value = name.value,
                onValueChange = { name.value = it },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            )

            Spacer(modifier = Modifier.height(16.dp))

            SleepTimeList(
                sleepTimes = sleepTimes,
                onEditClicked = { position, sleepTime ->
                    selectedSleepTime.value = position
                    showDialog.value = true
                },
                onRemoveClicked = { position: Int, sleepTime: SleepTime ->
                    Log.d("sleeptime length", sleepTimes.toString())
                    sleepTimes.removeAt(position)
                }
            )

        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)

        ) {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                contentPadding = PaddingValues(vertical = 18.dp),
                shape = RoundedCornerShape(13.dp),
                modifier = Modifier.weight(1f),
                onClick = {
                    try{
                        val sleepCycle = SleepCycle(name = name.value, sleepTimes = sleepTimes, isActive = 0)

                        if(sleepCycle.name.isEmpty()){
                            Toast.makeText(localContext, "Please enter the correct name", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        sleepCycleRepository.addSleepCycle(sleepCycle = sleepCycle)
                        navController.navigate("home")
                    }catch (e: Exception){
                        Log.e("exceptionally bad", e.message.toString())
                    }
                },

            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Save",
                        tint = AppColors.Slate
                    )
                    Text(
                        letterSpacing = 1.1.sp,
                        fontWeight = FontWeight(385),
                        fontSize = 16.sp,
                        text = "Save"
                    )
                }
            }

            Button(
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                contentPadding = PaddingValues(vertical = 18.dp),
                shape = RoundedCornerShape(13.dp),
                modifier = Modifier.weight(1f),
                    onClick = { showDialog.value = true },

            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timelapse,
                        contentDescription = "Add time",
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
            val currentSleepTime = selectedSleepTime.value?.let { sleepTimes[it] }
            TimeInputDialog(
                sleepTime = currentSleepTime,
                setShowDialog = { showDialog.value = it },
                onSave = { newSleepTime ->

                    val isValidSleepTime = sleepTimes.find { time ->
                        newSleepTime.isTimeInTimeFrame(time.startTime, time.calculateEndTime())
                    }

                    val isValidName = newSleepTime.name.isNotEmpty() && sleepTimes.none { it.name == newSleepTime.name }

                    if(!isValidName){
                        Toast.makeText(localContext, "Please fill name correctly", Toast.LENGTH_SHORT).show()
                        return@TimeInputDialog
                    }

                    if(isValidSleepTime != null){
                        Toast.makeText(localContext, "Please don't use overlapping sleep times", Toast.LENGTH_SHORT).show()
                        return@TimeInputDialog
                    }

                    selectedSleepTime.value?.let { index ->
                        sleepTimes[index] = newSleepTime
                    } ?: run {
                        sleepTimes.add(newSleepTime)
                    }

                },
                onDismiss = { showDialog.value = false }
            )
        }
    }
}



