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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import ru.gressor.gametimer.domain.ActiveTimer
import ru.gressor.gametimer.domain.Ticker
import ru.gressor.gametimer.utils.secondsToString
import ru.gressor.gametimer.utils.toUUID
import java.util.*

class TimerForegroundService : Service() {
    private var timer: ActiveTimer? = null
    private var job: Job? = null

    private var isServiceStarted = false
    private var notificationManager: NotificationManager? = null

    private val builder
        get() =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.hourglass)
                .setContentTitle(timer?.name ?: "Game Timer")
                .setGroup("Timer")
                .setGroupSummary(false)
                .setContentIntent(getPendingIntent())
                .setAutoCancel(false)
                .setSilent(true)

    private fun getPendingIntent(): PendingIntent? {
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent
            .getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT)
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    @DelicateCoroutinesApi
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processCommand(intent)
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @DelicateCoroutinesApi
    private fun processCommand(intent: Intent?) {
        when (intent?.extras?.getString(COMMAND_ID) ?: INVALID) {
            INVALID -> return
            COMMAND_STOP -> commandStop()
            COMMAND_START -> {
                val startTime = intent?.extras?.getLong(TIMER_FROM_ACTIVITY_VALUE) ?: return
                val id = intent.extras?.getString(TIMER_FROM_ACTIVITY_ID)?.toUUID() ?: return
                val name = intent.extras?.getString(TIMER_FROM_ACTIVITY_NAME) ?: "Current timer"
                commandStart(id, name, startTime)
            }
        }
    }

    @DelicateCoroutinesApi
    private fun commandStart(id: UUID, name: String, startTime: Long) {
        if (isServiceStarted) {
            return
        }
        try {
            moveToStartedState()
            startForegroundAndShowNotification()

            if (timer == null) timer = ActiveTimer(id, name, Ticker(startTime))
            timer?.ticker?.let {
                it.start()

                try {
                    job = GlobalScope.launch(Dispatchers.Main) {
                        it.flow.collectLatest { state ->
                            notificationManager?.notify(
                                NOTIFICATION_ID,
                                builder.setContentText(state.currentValue.secondsToString()).build()
                            )

                            if (state.currentValue <= 0L) {
                                cancel()
                            }
                        }
                    }
                } catch (e: Throwable) {
                    // cancel cause exception
                }
            }
        } finally {
            isServiceStarted = true
        }
    }

    private fun commandStop() {
        if (!isServiceStarted) {
            return
        }
        try {
            job?.cancel()
            timer?.ticker?.stop()
            stopForeground(true)
            stopSelf()
        } finally {
            isServiceStarted = false
        }
    }

    private fun moveToStartedState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, TimerForegroundService::class.java))
        } else {
            startService(Intent(this, TimerForegroundService::class.java))
        }
    }

    private fun startForegroundAndShowNotification() {
        createChannel()
        val notification = builder.setContentText("starting...").build()
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

        const val INVALID = "INVALID"
        const val COMMAND_START = "COMMAND_START"
        const val COMMAND_STOP = "COMMAND_STOP"
        const val COMMAND_ID = "COMMAND_ID"

        const val TIMER_FROM_ACTIVITY_VALUE = "TIMER_FROM_ACTIVITY_VALUE"
        const val TIMER_FROM_ACTIVITY_ID = "TIMER_FROM_ACTIVITY_ID"
        const val TIMER_FROM_ACTIVITY_NAME = "TIMER_FROM_ACTIVITY_NAME"
    }
}