import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.util.Log
import com.example.sleep_cycle.MainActivity
import com.example.sleep_cycle.R

class TimerService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.d("TimerService", "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TimerService", "Service started")

        createNotificationChannel()

        // Define a notification for the foreground service
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        // Build an ongoing notification
        val notification = NotificationCompat.Builder(this, "TIMER_SERVICE_CHANNEL")
            .setContentTitle("Sleep Cycle Timer")
            .setContentText("Counting down to next sleep time...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .setOngoing(true)  // Make the notification persistent (ongoing)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        startForeground(1, notification)

        // Return START_STICKY to restart the service if killed by the system
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "TIMER_SERVICE_CHANNEL",
                "Timer Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
