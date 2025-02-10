package com.example.sleep_cycle.data.viewmodels

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.models.SleepTime
import com.example.sleep_cycle.data.modules.Toaster
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import com.example.sleep_cycle.data.repository.SleepTimeRepository
import com.example.sleep_cycle.helpers.ErrorManager
import com.example.sleep_cycle.helpers.canAddSleepTime
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SleepCycleViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val sleepCycleRepository: SleepCycleRepository,
    private val sleepTimeRepository: SleepTimeRepository,
    private val errorManager: ErrorManager,
    private val toaster: Toaster
): BaseViewModel(appContext = appContext, toaster = toaster) {

    private val _sleepCycle = MutableLiveData<SleepCycle?>()
    val sleepCycle: MutableLiveData<SleepCycle?> get() = _sleepCycle

    private val _sleepTimes = MutableLiveData<MutableList<SleepTime>>(mutableListOf())
    val sleepTimes: MutableLiveData<MutableList<SleepTime>> get() = _sleepTimes

    fun setSleepCycle(sleepCycle: SleepCycle) {
        _sleepCycle.value = sleepCycle;
        _sleepTimes.value = sleepCycle.sleepTimes.toMutableList()
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

            _sleepTimes.value?.add(sleepTime)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSleepTime(updatedSleepTime: SleepTime) {
        val filteredSleepTimes = _sleepTimes.value?.filter {
            it.id != updatedSleepTime.id
        }
        _sleepTimes.value?.let { sleepTimes ->
            val overlappingTimeFrame = filteredSleepTimes?.find {
                it.isTimeInTimeFrame(updatedSleepTime.startTime, updatedSleepTime.calculateEndTime())
            }

            if (overlappingTimeFrame != null) {
                viewModelScope.launch {
                    errorManager.postError("Error: The updated SleepTime overlaps with another time frame.")
                }
                return
            }

            val requiredSleepTime = sleepTimes.find {
                it.id == updatedSleepTime.id
            }

            viewModelScope.launch {
                if (requiredSleepTime != null) {
                    sleepTimeRepository.updateSleepTime(updatedSleepTime)
                } else {
                    errorManager.postError("Error: Invalid position specified.")
                }
            }
        }
    }

    fun deleteSleepCycle(id: SleepCycle){
        viewModelScope.launch {
            try {
                sleepCycleRepository.deleteSleepCycle(id)

                if(id.id == activeSleepCycle.value?.id){
                    setActiveSleepCycle(null)
                }
                resetNotifAction()

            } catch (e: Exception) {
                errorManager.postError("Failed to delete sleep cycle")
            }
        }
    }

    fun removeSleepTime(sleepTime: SleepTime) {
        viewModelScope.launch {
            try {
                sleepTime.id?.let { sleepTimeRepository.deleteSleepTimeById(id = it) }

                resetNotifAction()
            } catch (e: Exception) {
                errorManager.postError("Failed to remove sleep time")
            }
        }
    }
}