package com.example.sleep_cycle.data.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.modules.Toaster
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewSleepCycleViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sleepCycleRepository: SleepCycleRepository,
    private val toaster: Toaster
): BaseViewModel(toaster = toaster, appContext = context) {

    fun createSleepCycle(sleepCycle: SleepCycle) {
        viewModelScope.launch {
            sleepCycleRepository.addSleepCycleWithTimes(sleepCycle)
        }
    }
}