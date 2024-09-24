package com.example.sleep_cycle.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.example.sleep_cycle.data.viewmodels.PreferenceViewModel

@Composable
fun Settings (
    navController: NavController, preferences: PreferenceViewModel
){
    val mode = preferences.modeFlow.collectAsState(initial = true)


    Column {

    }
}