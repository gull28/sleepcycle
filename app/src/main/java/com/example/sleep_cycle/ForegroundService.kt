package com.example.sleep_cycle

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.sleep_cycle.data.model.SleepTime
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import com.example.sleep_cycle.helper.Time
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForegroundService : Service() {

    @Inject
    lateinit var sleepCycleRepository: SleepCycleRepository // Inject the repository

    private var countdownTimer: CountDownTimer? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel() // Create notification channel on service creation
        registerReceiver(sleepCycleReceiver, IntentFilter("UPDATE_SLEEP_CYCLE"),
            RECEIVER_EXPORTED
       )
    }

    private val sleepCycleReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("34234234234", " Update2839482348")
            if (intent.action == "UPDATE_SLEEP_CYCLE") {
                fetchAndUpdateSleepTimes()
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundServiceWithNotification()

        // Fetch the upcoming sleep time and start the timer
        fetchAndUpdateSleepTimes()

        return START_STICKY
    }

    private fun fetchAndUpdateSleepTimes() {
        // cancel on refetch
        countdownTimer?.cancel()
        // Retrieve the next sleep time from the repository
        val nextSleepTime = sleepCycleRepository.getActiveSleepCycle()?.getNextSleepTime()

        if (nextSleepTime != null) {
            // Start the countdown if the sleep time is found
            startCountdown(nextSleepTime)
        } else {
            Log.d("ForegroundService", "No upcoming sleep time found.")
            stopSelf()
        }
    }

    // TODO: Make it so that it re-enables the notif, if the user toggles back the cycle
    // TODO: Create active sleep time tracking in the notif
    // ... i.e. if in sleep time, display time till end
    // for now, that is it

    private fun startCountdown(nextSleepTime: SleepTime) {
        val timeUntilNextSleep = calculateTimeUntilNextSleep(nextSleepTime)

        countdownTimer = object : CountDownTimer(timeUntilNextSleep, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateNotification(millisUntilFinished)
            }

            override fun onFinish() {
                fetchAndUpdateSleepTimes()
            }
        }.start()
    }

    private fun calculateTimeUntilNextSleep(nextSleepTime: SleepTime): Long {
        return Time.getTimeUntil(Time.stringToDateObj(nextSleepTime.startTime))
    }

    private fun startForegroundServiceWithNotification() {
        val notification = createNotification("Initializing timer...")
        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannelId = "SLEEP_CYCLE_NOTIFICATION"
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                "Sleep Cycle Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for Sleep Cycle Timer notifications"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun updateNotification(millisUntilFinished: Long) {
        val hours = millisUntilFinished / (1000 * 60 * 60)
        val minutes = (millisUntilFinished / (1000 * 60)) % 60
        val seconds = (millisUntilFinished / 1000) % 60

        // Format the time as HH:MM:SS
        val timeRemaining = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        val notification = createNotification("Next sleep time in $timeRemaining")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }


    private fun createNotification(contentText: String): Notification {
        val notificationChannelId = "SLEEP_CYCLE_NOTIFICATION"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(notificationChannelId, "Sleep Cycle Timer", NotificationManager.IMPORTANCE_LOW)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Sleep Cycle")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        countdownTimer?.cancel()
        unregisterReceiver(sleepCycleReceiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
