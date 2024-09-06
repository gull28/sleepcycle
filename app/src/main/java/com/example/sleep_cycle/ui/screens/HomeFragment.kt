package com.example.sleep_cycle.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.ui.components.SleepCycleList

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun HomeScreen(navController: NavController, viewModel: SleepCycleViewModel) {
    val context = LocalContext.current
    val sleepCycleRepository = SleepCycleRepository(context)
    val sleepCycles = sleepCycleRepository.getAllSleepCycles()

    Column {
        Button(onClick = { navController.navigate("newCycleScreen") }) {
            Text("Go to Second Screen")
        }

        SleepCycleList(sleepCycles, navController, viewModel)
    }
}

//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview() {
//    HomeScreen(navController = rememberNavController(), )
//}
