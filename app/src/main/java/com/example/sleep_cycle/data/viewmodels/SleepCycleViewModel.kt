package com.example.sleep_cycle.data.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.model.SleepTime
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SleepCycleViewModel @Inject constructor() : ViewModel() {

    init {
        Log.d("SleepCycleViewModel", "ViewModel initialized")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("SleepCycleViewModel", "ViewModel cleared")
    }

    private val _sleepCycle = MutableLiveData<SleepCycle>()
    val sleepCycle: LiveData<SleepCycle> get() = _sleepCycle

    private val _sleepTimes = MutableLiveData<MutableList<SleepTime>>(mutableListOf())
    val sleepTimes: LiveData<MutableList<SleepTime>> get() = _sleepTimes

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun setSleepCycle(sleepCycle: SleepCycle) {
        Log.d("asdasdasda123", sleepCycle.toString())
        _sleepCycle.value = sleepCycle
        _sleepTimes.value = sleepCycle.sleepTimes.toMutableList()
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun addSleepTime(sleepTime: SleepTime) {
        _sleepTimes.value?.let {
            it.add(sleepTime)
            _sleepTimes.value = it
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSleepTime(position: Int, updatedSleepTime: SleepTime) {
        _sleepTimes.value?.let { sleepTimes ->
            val overlappingTimeFrame = sleepTimes.find {
                it.isTimeInTimeFrame(updatedSleepTime.startTime, updatedSleepTime.calculateEndTime())
            }

            if (overlappingTimeFrame != null) {
                _errorMessage.value =
                    "Error: The updated SleepTime overlaps with another time frame."
                return
            }

            if (position in sleepTimes.indices) {
                sleepTimes[position] = updatedSleepTime
                _sleepTimes.value = sleepTimes
            } else {
                _errorMessage.value = "Error: Invalid position specified."
            }
        }
    }

    fun removeSleepTime(position: Int) {
        _sleepTimes.value?.let {
            it.removeAt(position)
            _sleepTimes.value = it
        }
    }
}