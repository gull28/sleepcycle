package com.example.sleep_cycle.data.viewmodels

import androidx.lifecycle.ViewModel
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class NewSleepCycleScreenViewModel @Inject constructor(
    @ApplicationContext private val context: ApplicationContext,
    private val sleepCycleRepository: SleepCycleRepository,
): ViewModel() {

}