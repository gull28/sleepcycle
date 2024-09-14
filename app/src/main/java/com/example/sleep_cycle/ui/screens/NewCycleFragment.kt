package com.example.sleep_cycle.ui.screens

import TimeInputDialog
import android.content.Context
import android.util.Log
import androidx.compose.ui.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.example.sleep_cycle.data.model.SleepTime
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.ui.components.SleepTimeList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCycleFragment(navController: NavController, viewModel: SleepCycleViewModel) {
    val sleepTimes = remember { mutableStateListOf<SleepTime>() }
    val selectedSleepTime = remember { mutableStateOf<Int?>(null) }
    val name = remember { mutableStateOf("") }
    val localContext = LocalContext.current
    val sleepCycleRepository =  SleepCycleRepository(localContext)

    rememberCoroutineScope()
    val showDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = name.value,
            onValueChange = { name.value = it },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Color.White,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showDialog.value = true },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add")
        }

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

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    try{
                        val sleepCycle = SleepCycle(name = name.value, sleepTimes = sleepTimes, isActive = 0)
                        Log.d("zazaz124123123", sleepTimes.toString())
                        val res = sleepCycleRepository.addSleepCycle(sleepCycle = sleepCycle)
                        Log.d("respose from savenewcycle", res.toString())
                        navController.navigate("home")
                    }catch (e: Exception){
                        Log.e("exceptionally bad", e.message.toString())
                    }
                },

            ) {
                Text("Save")
            }

            Button(
                onClick = { showDialog.value = true },
            ) {
                Text("Add")
            }
        }

        if (showDialog.value) {
            val currentSleepTime = selectedSleepTime.value?.let { sleepTimes[it] }
            TimeInputDialog(
                sleepTime = currentSleepTime,
                setShowDialog = { showDialog.value = it },
                onSave = { newSleepTime ->
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

private fun onSaveClicked(
    name: String,
    sleepTimes: List<SleepTime>,
    context: Context,
    navController: NavController
) {
}



