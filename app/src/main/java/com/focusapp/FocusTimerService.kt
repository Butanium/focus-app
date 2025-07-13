package com.focusapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat

class FocusTimerService : Service() {
    companion object {
        const val CHANNEL_ID = "FocusTimerChannel"
        const val NOTIFICATION_ID = 1
        const val REMINDER_NOTIFICATION_ID = 2
    }

    private lateinit var prefs: SharedPreferences
    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
    private var startTime = 0L
    private var isTimerActive = false
    private var isOnCall = false

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences("focus_app_prefs", Context.MODE_PRIVATE)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "PAUSE" -> pauseTimer()
            "RESUME" -> resumeTimer()
            "UPDATE_SETTINGS" -> updateSettings()
            "START_TIMER" -> startTimer()
            "STOP_TIMER" -> stopTimer()
            "CALL_STARTED" -> onCallStarted()
            "CALL_ENDED" -> onCallEnded()
            else -> {
                if (!isTimerActive && !prefs.getBoolean("timer_paused", false)) {
                    startTimer()
                }
            }
        }
        
        startForeground(NOTIFICATION_ID, createServiceNotification())
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.notification_channel_desc)
                enableVibration(true)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createServiceNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val status = when {
            prefs.getBoolean("timer_paused", false) -> "En pause"
            isOnCall -> "Pause (appel)"
            isTimerActive -> "Actif"
            else -> "Inactif"
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Focus App")
            .setContentText("Timer: $status")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun startTimer() {
        if (isTimerActive || prefs.getBoolean("timer_paused", false) || isOnCall) return
        
        isTimerActive = true
        startTime = System.currentTimeMillis()
        
        val delayMinutes = prefs.getInt("initial_delay", 5)
        
        timerRunnable = Runnable {
            showReminderNotification(delayMinutes)
            isTimerActive = false
        }
        
        handler.postDelayed(timerRunnable!!, delayMinutes * 60 * 1000L)
        updateServiceNotification()
    }

    private fun stopTimer() {
        isTimerActive = false
        timerRunnable?.let { handler.removeCallbacks(it) }
        timerRunnable = null
        updateServiceNotification()
    }

    private fun pauseTimer() {
        stopTimer()
        updateServiceNotification()
    }

    private fun resumeTimer() {
        if (!prefs.getBoolean("timer_paused", false) && !isOnCall) {
            startTimer()
        }
    }

    private fun onCallStarted() {
        isOnCall = true
        stopTimer()
        updateServiceNotification()
    }

    private fun onCallEnded() {
        isOnCall = false
        if (!prefs.getBoolean("timer_paused", false)) {
            startTimer()
        }
        updateServiceNotification()
    }

    private fun updateSettings() {
        if (isTimerActive) {
            stopTimer()
            if (!prefs.getBoolean("timer_paused", false) && !isOnCall) {
                startTimer()
            }
        }
    }

    private fun updateServiceNotification() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, createServiceNotification())
    }

    private fun showReminderNotification(minutesElapsed: Int) {
        if (prefs.getBoolean("vibration_enabled", true)) {
            vibrate()
        }

        val shortReminderAction = createNotificationAction("SHORT_REMINDER", getString(R.string.action_remind_5))
        val longReminderAction = createNotificationAction("LONG_REMINDER", getString(R.string.action_remind_15))
        val disableAction = createNotificationAction("DISABLE", getString(R.string.action_disable))

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text, minutesElapsed))
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(shortReminderAction)
            .addAction(longReminderAction)
            .addAction(disableAction)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(REMINDER_NOTIFICATION_ID, notification)
    }

    private fun createNotificationAction(action: String, title: String): NotificationCompat.Action {
        val intent = Intent(this, NotificationActionReceiver::class.java).apply {
            this.action = action
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, action.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Action.Builder(0, title, pendingIntent).build()
    }

    private fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }
}