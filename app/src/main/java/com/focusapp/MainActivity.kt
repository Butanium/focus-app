package com.focusapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.focusapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences
    private var isTimerPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("focus_app_prefs", Context.MODE_PRIVATE)
        
        loadSettings()
        updateUI()
        
        binding.pauseResumeButton.setOnClickListener {
            toggleTimer()
        }

        binding.whitelistButton.setOnClickListener {
            val intent = Intent(this, WhitelistActivity::class.java)
            startActivity(intent)
        }

        binding.initialDelayEdit.setOnFocusChangeListener { _, _ -> saveSettings() }
        binding.shortReminderEdit.setOnFocusChangeListener { _, _ -> saveSettings() }
        binding.longReminderEdit.setOnFocusChangeListener { _, _ -> saveSettings() }
        binding.vibrationSwitch.setOnCheckedChangeListener { _, _ -> saveSettings() }

        startFocusService()
    }

    private fun loadSettings() {
        binding.initialDelayEdit.setText(prefs.getInt("initial_delay", 5).toString())
        binding.shortReminderEdit.setText(prefs.getInt("short_reminder", 5).toString())
        binding.longReminderEdit.setText(prefs.getInt("long_reminder", 15).toString())
        binding.vibrationSwitch.isChecked = prefs.getBoolean("vibration_enabled", true)
        isTimerPaused = prefs.getBoolean("timer_paused", false)
    }

    private fun saveSettings() {
        try {
            val initialDelay = binding.initialDelayEdit.text.toString().toIntOrNull() ?: 5
            val shortReminder = binding.shortReminderEdit.text.toString().toIntOrNull() ?: 5
            val longReminder = binding.longReminderEdit.text.toString().toIntOrNull() ?: 15
            
            prefs.edit()
                .putInt("initial_delay", initialDelay.coerceIn(1, 30))
                .putInt("short_reminder", shortReminder.coerceIn(1, 30))
                .putInt("long_reminder", longReminder.coerceIn(1, 60))
                .putBoolean("vibration_enabled", binding.vibrationSwitch.isChecked)
                .apply()
                
            notifyServiceOfSettingsChange()
        } catch (e: Exception) {
            Toast.makeText(this, "Erreur dans les réglages", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleTimer() {
        isTimerPaused = !isTimerPaused
        prefs.edit().putBoolean("timer_paused", isTimerPaused).apply()
        
        val intent = Intent(this, FocusTimerService::class.java)
        intent.action = if (isTimerPaused) "PAUSE" else "RESUME"
        startService(intent)
        
        updateUI()
    }

    private fun updateUI() {
        binding.pauseResumeButton.text = if (isTimerPaused) "REPRENDRE" else "PAUSE"
        binding.statusText.text = if (isTimerPaused) "Timer: En pause" else "Timer: Actif"
    }

    private fun startFocusService() {
        val intent = Intent(this, FocusTimerService::class.java)
        startForegroundService(intent)
    }

    private fun notifyServiceOfSettingsChange() {
        val intent = Intent(this, FocusTimerService::class.java)
        intent.action = "UPDATE_SETTINGS"
        startService(intent)
    }
}