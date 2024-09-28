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
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.sleep_cycle.data.models.SleepTime
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import com.example.sleep_cycle.helper.Time
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class ForegroundService : Service() {

    @Inject
    lateinit var sleepCycleRepository: SleepCycleRepository

    private var countdownTimer: CountDownTimer? = null
    private val syncHandler = Handler(Looper.getMainLooper())
    private lateinit var syncRunnable: Runnable
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        registerReceiver(sleepCycleReceiver, IntentFilter("UPDATE_SLEEP_CYCLE"),
            RECEIVER_EXPORTED
       )

        syncRunnable = Runnable {
            fetchAndUpdateSleepTimes()
            syncHandler.postDelayed(syncRunnable, 5 * 60 * 1000) // 5 minutes
        }

        syncHandler.post(syncRunnable)
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
        countdownTimer = null

        serviceScope.launch(Dispatchers.IO) {
            val activeSleepCycle = sleepCycleRepository.getActiveSleepCycle()
            val currentTime = LocalTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

            val activeSleepTime = activeSleepCycle?.sleepTimes?.find { sleepTime ->
                sleepTime.isTimeInTimeFrame(currentTime.format(formatter), currentTime.format(formatter))
            }

            val timeUntilEnd = activeSleepTime?.let {
                TimeRange(it.startTime, it.calculateEndTime()).millisUntilEnd(currentTime.format(formatter))
            }

            withContext(Dispatchers.Main) {
                if (timeUntilEnd != null) {
                    startCountdown(timeUntilEnd, "Sleep time ends in ")
                } else {
                    val nextSleepTime = activeSleepCycle?.getNextSleepTime()
                    val timeUntilNextSleep = nextSleepTime?.let { calculateTimeUntilNextSleep(it) }

                    if (timeUntilNextSleep != null) {
                        startCountdown(timeUntilNextSleep, "Next sleep time in ")
                    } else {
                        stopSelf()
                    }
                }
            }
        }
    }


    private fun startCountdown(timeUntil: Long, text: String) {
        countdownTimer?.cancel()
        countdownTimer = object : CountDownTimer(timeUntil, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateNotification(millisUntilFinished, text)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFinish() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    val vibrator = vibratorManager.defaultVibrator
                    val vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                    vibrator.vibrate(vibrationEffect)
                } else {
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                        vibrator.vibrate(vibrationEffect)
                    } else {
                        vibrator.vibrate(500)
                    }
                }

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
