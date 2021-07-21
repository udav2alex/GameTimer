package ru.gressor.gametimer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class TimerForegroundService : Service() {
    private var isServiceStarted = false
    private var notificationManager: NotificationManager? = null

    private val builder by lazy {
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.hourglass)
            .setContentTitle("Game Timer")
            .setGroup("Timer")
            .setGroupSummary(false)
            .setContentIntent(getPendingIntent())
            .setAutoCancel(false)
            .setSilent(true)
    }

    private fun getPendingIntent(): PendingIntent? {
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
        return PendingIntent
            .getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT)
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processCommand(intent)
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun processCommand(intent: Intent?) {
        when (intent?.extras?.getString(COMMAND_ID) ?: INVALID) {
            INVALID -> return
            COMMAND_STOP -> commandStop()
            COMMAND_START -> {
                val startTime = intent?.extras?.getLong(STARTED_TIMER_TIME_MS) ?: return
                commandStart(startTime)
            }
        }
    }

    private fun commandStop() {
        if (!isServiceStarted) {
            return
        }
        try {
//            job?.cancel()
            stopForeground(true)
            stopSelf()
        } finally {
            isServiceStarted = false
        }
    }

    private fun commandStart(startTime: Long) {
        if (isServiceStarted) {
            return
        }
        try {
            moveToStartedState()
            startForegroundAndShowNotification()
            notificationManager?.notify(NOTIFICATION_ID, builder.setContentText("content").build())
//            continueTimer(startTime)
        } finally {
            isServiceStarted = true
        }
    }

//    private fun continueTimer(startTime: Long) {
//        job = GlobalScope.launch(Dispatchers.Main) {
//            while (true) {
//                notificationManager?.notify(
//                    NOTIFICATION_ID,
//                    getNotification(
//                        (System.currentTimeMillis() - startTime).displayTime().dropLast(3)
//                    )
//                )
//                delay(INTERVAL)
//            }
//        }
//    }

    private fun moveToStartedState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, TimerForegroundService::class.java))
        } else {
            startService(Intent(this, TimerForegroundService::class.java))
        }
    }

    private fun startForegroundAndShowNotification() {
        createChannel()
        val notification = builder.setContentText("content").build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Game Timer foreground notification"
            }

            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 42
        private const val CHANNEL_ID = "TimerForegroundService"
        private const val CHANNEL_NAME = "Game Timer Channel"

        const val TIMER_DATA_KEY = "TIMER_DATA_KEY"

        const val INVALID = "INVALID"
        const val COMMAND_START = "COMMAND_START"
        const val COMMAND_STOP = "COMMAND_STOP"
        const val COMMAND_ID = "COMMAND_ID"
        const val STARTED_TIMER_TIME_MS = "STARTED_TIMER_TIME_MS"
    }
}