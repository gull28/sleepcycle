package com.example.sleep_cycle.data.viewmodels

import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sleep_cycle.ForegroundService
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.models.SleepTime
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import com.example.sleep_cycle.data.modules.Toaster
import com.example.sleep_cycle.data.repository.SleepTimeRepository
import com.example.sleep_cycle.helpers.ErrorManager
import com.example.sleep_cycle.helpers.canAddSleepTime

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sleepCycleRepository: SleepCycleRepository,
    private val sleepTimeRepository: SleepTimeRepository,
    private val toaster: Toaster,
    @ApplicationContext private val appContext: Context,
    private val errorManager: ErrorManager,
    ) : BaseViewModel(appContext, toaster) {


    // set all cycles
    private val _sleepCycles = MutableLiveData<List<SleepCycle>>()
    val sleepCycles: LiveData<List<SleepCycle>> get() = _sleepCycles


    private val _sleepTimes = MutableLiveData<MutableList<SleepTime>>(mutableListOf())
    val sleepTimes: LiveData<MutableList<SleepTime>> get() = _sleepTimes

    init {
        Log.d("SleepCycleViewModel", "ViewModel initialized")
        getAllSleepCycles()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("SleepCycleViewModel", "ViewModel cleared")
    }

    private fun getAllSleepCycles(): List<SleepCycle>? {
        viewModelScope.launch {
            val cycles = sleepCycleRepository.getAllSleepCycles()
            _sleepCycles.value = cycles

            val activeCycle = cycles.find { it.isActive == 1 }

            if (activeCycle != null)
                _activeSleepCycle.value = activeCycle
        }

        return _sleepCycles.value;
    }

    fun toggleActive(id: Long, isActive: Int) {

        viewModelScope.launch {
            // find the sleepCycle
            try {
                sleepCycleRepository.toggleActive(id, isActive)

                val activeCycle = sleepCycleRepository.getActiveSleepCycle()
                // set it as active in the ViewModel
                setActiveSleepCycle(activeCycle)

            }catch (e: Exception) {
                errorManager.postError("Failed to toggle active sleep cycle")
            }
        }
    }
}
