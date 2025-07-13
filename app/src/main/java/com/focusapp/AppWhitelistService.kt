package com.focusapp

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.accessibility.AccessibilityEvent

class AppWhitelistService : AccessibilityService() {
    private lateinit var prefs: SharedPreferences
    private var lastForegroundApp: String? = null
    private var isInWhitelistedApp = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefs = getSharedPreferences("focus_app_prefs", Context.MODE_PRIVATE)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()
            
            if (packageName != null && packageName != lastForegroundApp) {
                lastForegroundApp = packageName
                handleAppSwitch(packageName)
            }
        }
    }

    override fun onInterrupt() {}

    private fun handleAppSwitch(packageName: String) {
        val whitelistedApps = prefs.getStringSet("whitelisted_apps", emptySet()) ?: emptySet()
        val wasInWhitelistedApp = isInWhitelistedApp
        isInWhitelistedApp = whitelistedApps.contains(packageName)

        val serviceIntent = Intent(this, FocusTimerService::class.java)

        when {
            !wasInWhitelistedApp && isInWhitelistedApp -> {
                serviceIntent.action = "PAUSE"
                startService(serviceIntent)
            }
            wasInWhitelistedApp && !isInWhitelistedApp -> {
                serviceIntent.action = "RESUME"
                startService(serviceIntent)
            }
        }
    }
}