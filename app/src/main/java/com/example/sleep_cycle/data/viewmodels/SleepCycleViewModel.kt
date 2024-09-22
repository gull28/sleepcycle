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
import com.example.sleep_cycle.data.model.SleepTime
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import com.example.sleep_cycle.data.repository.SleepTimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context

@HiltViewModel
class SleepCycleViewModel @Inject constructor(
    private val sleepCycleRepository: SleepCycleRepository,
    private val sleepTimeRepository: SleepTimeRepository,
    @ApplicationContext private val appContext: Context,

) : ViewModel() {

    init {
        Log.d("SleepCycleViewModel", "ViewModel initialized")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("SleepCycleViewModel", "ViewModel cleared")
    }

    // set all cycles
    private val _sleepCycles = MutableLiveData<List<SleepCycle>>()
    val sleepCycles: LiveData<List<SleepCycle>> get() = _sleepCycles

    // this is selected  sleep cycle
    private val _sleepCycle = MutableLiveData<SleepCycle>()
    val sleepCycle: LiveData<SleepCycle> get() = _sleepCycle

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

    fun removeSleepTime(id: Long) {
        try {
            val result = sleepTimeRepository.deleteSleepTime(id)

            loadSleepTimes()

            resetNotifAction()
            return
        }

        catch (e: Exception){
            Log.e("SleepCycleViewModel", e.message.toString())
            return
        }
    }

    fun deleteSleepCycle(id: Long){
        val result = sleepCycleRepository.deleteSleepCycle(id)

        if(id == activeSleepCycle.value?.id){
            setActiveSleepCycle(null)
        }

        resetNotifAction()
        if(result){
            Log.d("SleepCycleViewModel", "Sleep cycle deleted successfully.")
            return
        }
        _errorMessage.value = "Error: Unable to delete sleep cycle."
    }

    fun resetNotifAction(){
        val broadcastIntent = Intent("UPDATE_SLEEP_CYCLE")
        appContext.sendBroadcast(broadcastIntent)
    }

    fun getAllSleepCycles(): List<SleepCycle>? {
        viewModelScope.launch {
            val cycles = sleepCycleRepository.getAllSleepCycles()  // Assuming this returns List<SleepCycle>
            _sleepCycles.value = cycles

            val activeCycle = cycles.find { it.isActive == 1 }

            if (activeCycle != null)
                _activeSleepCycle.value = activeCycle
        }

        return _sleepCycles.value;

        // find the active cycle
    }

    fun resetNotifAction(){
        val broadcastIntent = Intent("UPDATE_SLEEP_CYCLE")
        appContext.sendBroadcast(broadcastIntent)
    }

    fun toggleActive(id: Long) {
        // find the sleepCycle
        val result = sleepCycleRepository.toggleActive(id)

        val activeCycle = sleepCycleRepository.getSleepCycleById(id)
        // set it as active in the viewmodel
        setActiveSleepCycle(activeCycle)
        // profit
        if (result) {
            Log.d("SleepCycleViewModel", "Sleep cycle toggled successfully.")
        } else {
            _errorMessage.value = "Error: Unable to toggle sleep cycle."
        }
    }


    private fun loadSleepTimes() {
        // Load sleep times from repository and update the LiveData
        viewModelScope.launch {
            _sleepTimes.value =
                sleepCycle.value?.id?.let { sleepCycleRepository.getSleepCycleById(it)?.sleepTimes } as MutableList<SleepTime>?
        }
    }
}
