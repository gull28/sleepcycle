package com.example.sleep_cycle.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.ui.components.SleepCycleList
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.sleep_cycle.ui.components.Clock

@Composable
fun HomeScreen(navController: NavController, viewModel: SleepCycleViewModel) {
    val context = LocalContext.current
    val sleepCycleRepository = SleepCycleRepository(context)
    val sleepCycles = sleepCycleRepository.getAllSleepCycles()

    // Wrap the content in a scrollable Box
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
                Text("Go to Second Screen")
            }

            // TODO: Create conditional render as well as display time slots for active cycle
            Clock()

            Spacer(modifier = Modifier.height(16.dp))

            SleepCycleList(sleepCycles, navController, viewModel, limit = 3)
        }
    }
}

