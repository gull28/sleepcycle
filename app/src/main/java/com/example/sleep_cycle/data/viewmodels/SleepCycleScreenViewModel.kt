package com.example.sleep_cycle.data.viewmodels

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.models.SleepTime
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import com.example.sleep_cycle.data.repository.SleepTimeRepository
import com.example.sleep_cycle.helpers.ErrorManager
import com.example.sleep_cycle.helpers.canAddSleepTime
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SleepCycleScreenViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val sleepCycleRepository: SleepCycleRepository,
    private val sleepTimeRepository: SleepTimeRepository,
    private val errorManager: ErrorManager
): ViewModel() {

    private val _sleepCycle = MutableLiveData<SleepCycle?>()
    val sleepCycle: MutableLiveData<SleepCycle?> get() = _sleepCycle

    fun setSleepCycle(sleepCycle: SleepCycle) {
        _sleepCycle.value = sleepCycle;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addSleepTime(sleepTime: SleepTime) {

        viewModelScope.launch{
            sleepTime.scheduleId = sleepCycle.value?.id

            val result = canAddSleepTime(_sleepCycle.value?.sleepTimes?.toMutableList() ?: mutableListOf(), sleepTime)

            if(result.isValid) {
                sleepTimeRepository.addSleepTime(sleepTime)
            } else {
                result.message?.let { errorManager.postError(it) }
                return@launch
            }

            _sleepCycle.value?.sleepTimes?.let {
                it.add(sleepTime)
                _sleepTimes.value = it
            }
        }
    }
}