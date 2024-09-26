package com.example.sleep_cycle

import TimeRange
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
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class ForegroundService : Service() {

    @Inject
    lateinit var sleepCycleRepository: SleepCycleRepository // Inject the repository

    private var countdownTimer: CountDownTimer? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        registerReceiver(sleepCycleReceiver, IntentFilter("UPDATE_SLEEP_CYCLE"),
            RECEIVER_EXPORTED
       )
    }

    private val sleepCycleReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "UPDATE_SLEEP_CYCLE") {
                fetchAndUpdateSleepTimes()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundServiceWithNotification()

        fetchAndUpdateSleepTimes()

        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchAndUpdateSleepTimes() {
        countdownTimer?.cancel()

        val activeSleepCycle = sleepCycleRepository.getActiveSleepCycle()
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        val activeSleepTime = activeSleepCycle?.sleepTimes?.find { sleepTime ->
            sleepTime.isTimeInTimeFrame(currentTime.format(formatter), currentTime.format(formatter))
        }

        if (activeSleepTime != null) {
            val timeUntilEnd = TimeRange(activeSleepTime.startTime, activeSleepTime.calculateEndTime()).millisUntilEnd(currentTime.format(formatter))
            startCountdown(timeUntilEnd, "Sleep time ends in ")
            return
        }

        val nextSleepTime = activeSleepCycle?.getNextSleepTime()

        if (nextSleepTime != null) {
            val timeUntilNextSleep = calculateTimeUntilNextSleep(nextSleepTime)
            startCountdown(timeUntilNextSleep, "Next sleep time in ")
        } else {
            Log.d("ForegroundService", "No upcoming sleep time found.")
            stopSelf() // Ensure to stop the service if no sleep times are available
        }
    }


    private fun startCountdown(timeUntil: Long, text: String) {
        countdownTimer = object : CountDownTimer(timeUntil, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateNotification(millisUntilFinished, text)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFinish() {
                Log.d("finished", "finished")
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

    private fun updateNotification(millisUntilFinished: Long, text: String) {
        val hours = millisUntilFinished / (1000 * 60 * 60)
        val minutes = (millisUntilFinished / (1000 * 60)) % 60
        val seconds = (millisUntilFinished / 1000) % 60

        // Format the time as HH:MM:SS
        val timeRemaining = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        val notification = createNotification("$text $timeRemaining")
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
            .setShowWhen(false)
            .build()
    }

    override fun onDestroy() {
        countdownTimer?.cancel()
        unregisterReceiver(sleepCycleReceiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
