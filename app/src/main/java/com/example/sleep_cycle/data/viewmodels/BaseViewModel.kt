package com.example.sleep_cycle.data.viewmodels

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sleep_cycle.ForegroundService
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.modules.Toaster
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val toaster: Toaster,
): ViewModel() {

    protected val _activeSleepCycle = MutableLiveData<SleepCycle?>()
    val activeSleepCycle: MutableLiveData<SleepCycle?> get() = _activeSleepCycle

    fun resetNotifAction(){
        val broadcastIntent = Intent("UPDATE_SLEEP_CYCLE")
        appContext.sendBroadcast(broadcastIntent)
    }

    fun showToast(message: String) {
        toaster.showToast(message)
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
}