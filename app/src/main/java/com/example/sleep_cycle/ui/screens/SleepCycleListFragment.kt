package com.example.sleep_cycle.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.viewmodels.HomeViewModel
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.ui.components.SleepCycleList

@Composable

fun SleepCycleListFragment(navController: NavController, viewModel: HomeViewModel, onSleepCycleClick: (SleepCycle) -> Unit) {
    val sleepCycles by viewModel.sleepCycles.observeAsState(initial = emptyList())

    Column (
        modifier = Modifier.padding(horizontal = 8.dp)
    ){
        SleepCycleList(sleepCycles = sleepCycles , navController = navController, sleepCycleViewModel = viewModel, onSleepCycleClick = {
            onSleepCycleClick(it)
        })
    }
}