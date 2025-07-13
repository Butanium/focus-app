package com.focusapp

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(FocusTimerService.REMINDER_NOTIFICATION_ID)
        
        val prefs = context.getSharedPreferences("focus_app_prefs", Context.MODE_PRIVATE)
        
        when (intent.action) {
            "SHORT_REMINDER" -> {
                val shortDelay = prefs.getInt("short_reminder", 5)
                scheduleNextReminder(context, shortDelay)
            }
            
            "LONG_REMINDER" -> {
                val longDelay = prefs.getInt("long_reminder", 15)
                scheduleNextReminder(context, longDelay)
            }
            
            "DISABLE" -> {
                prefs.edit().putBoolean("timer_paused", true).apply()
                val serviceIntent = Intent(context, FocusTimerService::class.java)
                serviceIntent.action = "PAUSE"
                context.startService(serviceIntent)
            }
        }
    }
    
    private fun scheduleNextReminder(context: Context, delayMinutes: Int) {
        Handler(Looper.getMainLooper()).postDelayed({
            val serviceIntent = Intent(context, FocusTimerService::class.java)
            serviceIntent.action = "START_TIMER"
            context.startService(serviceIntent)
        }, delayMinutes * 60 * 1000L)
    }
}