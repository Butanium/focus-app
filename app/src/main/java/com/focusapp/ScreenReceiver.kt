package com.focusapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager

class ScreenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_ON, Intent.ACTION_USER_PRESENT -> {
                val serviceIntent = Intent(context, FocusTimerService::class.java)
                serviceIntent.action = "START_TIMER"
                context.startForegroundService(serviceIntent)
            }
            
            TelephonyManager.ACTION_PHONE_STATE_CHANGED -> {
                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                val serviceIntent = Intent(context, FocusTimerService::class.java)
                
                when (state) {
                    TelephonyManager.EXTRA_STATE_RINGING,
                    TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                        serviceIntent.action = "CALL_STARTED"
                    }
                    TelephonyManager.EXTRA_STATE_IDLE -> {
                        serviceIntent.action = "CALL_ENDED"
                    }
                }
                
                context.startService(serviceIntent)
            }
        }
    }
}