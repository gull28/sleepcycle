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

@HiltViewModel
open class SleepCycleViewModel @Inject constructor(
    private val sleepCycleRepository: SleepCycleRepository,
    private val sleepTimeRepository: SleepTimeRepository,
    private val toaster: Toaster,
    @ApplicationContext private val appContext: Context,

    ) : ViewModel() {

    init {
        Log.d("SleepCycleViewModel", "ViewModel initialized")
    }

    fun showToast(message: String) {
        toaster.showToast(message)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("SleepCycleViewModel", "ViewModel cleared")
    }

    // set all cycles
    private val _sleepCycles = MutableLiveData<List<SleepCycle>>()
    open val sleepCycles: LiveData<List<SleepCycle>> get() = _sleepCycles

    // this is selected  sleep cycle
    private val _sleepCycle = MutableLiveData<SleepCycle?>()
    val sleepCycle: MutableLiveData<SleepCycle?> get() = _sleepCycle

    // this is for active sleep cycle (toggled on)
    private val _activeSleepCycle = MutableLiveData<SleepCycle?>()
    val activeSleepCycle: MutableLiveData<SleepCycle?> get() = _activeSleepCycle

    private val _sleepTimes = MutableLiveData<MutableList<SleepTime>>(mutableListOf())
    val sleepTimes: LiveData<MutableList<SleepTime>> get() = _sleepTimes

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun setSleepCycle(sleepCycle: SleepCycle) {
        _sleepCycle.value = sleepCycle
        _sleepTimes.value = sleepCycle.sleepTimes.toMutableList()
    }

    fun setActiveSleepCycle(sleepCycle: SleepCycle?){
        _activeSleepCycle.value = sleepCycle

        // this is the case for when the user disables the notification when de-toggling a cycle
        // get notif
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val existingNotification = notificationManager.activeNotifications.find { it.id == 1 }

        // if doesnt exist create one
        if (existingNotification == null) {
            val serviceIntent = Intent(appContext, ForegroundService::class.java)
            ContextCompat.startForegroundService(appContext, serviceIntent)

            resetNotifAction()
        } else {
            // otherwise broadcast change
            resetNotifAction()
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun addSleepTime(sleepTime: SleepTime, scheduleId: Long) {

        viewModelScope.launch{
            sleepTime.scheduleId = scheduleId
            sleepTimeRepository.addSleepTime(sleepTime)

            _sleepTimes.value?.let {
                it.add(sleepTime)
                _sleepTimes.value = it
            }
        }
    }

    open fun addSleepCycle(sleepCycle: SleepCycle) {
        viewModelScope.launch {
            sleepCycleRepository.addSleepCycleWithTimes(sleepCycle)
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
                _errorMessage.value =
                    "Error: The updated SleepTime overlaps with another time frame."
                return
            }

            val requiredSleepTime = sleepTimes.find {
                it.id == updatedSleepTime.id
            }

            if (requiredSleepTime != null) {
                viewModelScope.launch {
                    val result = sleepTimeRepository.updateSleepTime(updatedSleepTime)

                    getAllSleepCycles()
                }
            } else {
                _errorMessage.value = "Error: Invalid position specified."
            }
        }
    }

    fun removeSleepTime(id: Long) {
        viewModelScope.launch {
            try {
                val result = sleepTimeRepository.deleteSleepTimeById(id)

                getAllSleepCycles()

                resetNotifAction()
            } catch (e: Exception) {
                Log.e("SleepCycleViewModel", e.message.toString())
            }
        }
    }

    fun deleteSleepCycle(id: SleepCycle){
        viewModelScope.launch {
            try {
                val result = sleepCycleRepository.deleteSleepCycle(id)

                if(id.id == activeSleepCycle.value?.id){
                    setActiveSleepCycle(null)
                }
                getAllSleepCycles()
                resetNotifAction()

            } catch (e: Exception) {
                Log.e("123123123dsdff", e.message.toString())
            }
        }
    }

    fun resetNotifAction(){
        val broadcastIntent = Intent("UPDATE_SLEEP_CYCLE")
        appContext.sendBroadcast(broadcastIntent)
    }

    fun getAllSleepCycles(): List<SleepCycle>? {
        viewModelScope.launch {
            val cycles = sleepCycleRepository.getAllSleepCycles()
            _sleepCycles.value = cycles

            val activeCycle = cycles.find { it.isActive == 1 }

            if (activeCycle != null)
                _activeSleepCycle.value = activeCycle

            if(sleepCycle.value != null){
                val selectedSleepCycle = cycles.find { it.id == sleepCycle.value!!.id }

                if (selectedSleepCycle != null){
                    _sleepCycle.value = selectedSleepCycle
                    _sleepTimes.value = selectedSleepCycle.sleepTimes.toMutableList()

                }

            }
        }

        return _sleepCycles.value;
    }

    fun toggleActive(id: Long, isActive: Int) {
        viewModelScope.launch {
            // find the sleepCycle
            val result = sleepCycleRepository.toggleActive(id, isActive)

            val activeCycle = sleepCycleRepository.getActiveSleepCycle()
            // set it as active in the ViewModel
            setActiveSleepCycle(activeCycle)

            // Profit
//            if () {
//                Log.d("SleepCycleViewModel", "Sleep cycle toggled successfully.")
//            } else {
//                _errorMessage.value = "Error: Unable to toggle sleep cycle."
//            }
        }
    }

    private fun loadSleepTimes() {
        viewModelScope.launch {
            val sleepCycleId = sleepCycle.value?.id
            if (sleepCycleId != null) {
                val sleepCycleWithTimes = sleepCycleRepository.getSleepCycleById(sleepCycleId)
                _sleepTimes.value = sleepCycleWithTimes?.sleepTimes?.toMutableList() ?: mutableListOf()
            } else {
                _sleepTimes.value = mutableListOf() // Fallback in case there's no selected sleep cycle
            }
        }
    }
}
