package com.example.sleep_cycle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sleep_cycle.data.viewmodels.PreferenceViewModel
import com.example.sleep_cycle.ui.theme.AppColors

@Composable
fun Settings (
    navController: NavController, preferences: PreferenceViewModel
){
    val mode = preferences.modeFlow.collectAsState(initial = true)

    Column(
        modifier = Modifier.background(AppColors.Background).fillMaxSize().padding(horizontal = 24.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Clock/Timer",
                color = AppColors.Slate,
                fontWeight = FontWeight(400),
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = mode.value,
                onCheckedChange = { check ->
                    preferences.saveMode(check)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AppColors.Primary,
                    checkedTrackColor = AppColors.Slate
                )
            )
        }
    }
}