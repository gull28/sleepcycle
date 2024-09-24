package com.example.sleep_cycle.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.ui.components.SleepCycleList

@Composable

fun SleepCycleListFragment(navController: NavController, viewModel: SleepCycleViewModel) {

    val context = LocalContext.current
    val sleepCycleRepository = SleepCycleRepository(context)

    val sleepCycles = sleepCycleRepository.getAllSleepCycles()
    Column {
        SleepCycleList(sleepCycles =sleepCycles , navController = navController, sleepCycleViewModel = viewModel)
    }
}