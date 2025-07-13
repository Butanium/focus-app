package com.focusapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class WhitelistActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences
    private lateinit var listView: ListView
    private val installedApps = mutableListOf<AppInfo>()
    private val whitelistedApps = mutableSetOf<String>()

    data class AppInfo(val packageName: String, val appName: String, var isWhitelisted: Boolean)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whitelist)

        prefs = getSharedPreferences("focus_app_prefs", Context.MODE_PRIVATE)
        listView = findViewById(R.id.appListView)

        loadInstalledApps()
        setupListView()
        checkAccessibilityPermission()
    }

    private fun loadInstalledApps() {
        val pm = packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val savedWhitelist = prefs.getStringSet("whitelisted_apps", emptySet()) ?: emptySet()
        whitelistedApps.addAll(savedWhitelist)

        installedApps.clear()
        for (packageInfo in packages) {
            if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                val appName = pm.getApplicationLabel(packageInfo).toString()
                val isWhitelisted = whitelistedApps.contains(packageInfo.packageName)
                installedApps.add(AppInfo(packageInfo.packageName, appName, isWhitelisted))
            }
        }
        installedApps.sortBy { it.appName }
    }

    private fun setupListView() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_multiple_choice,
            installedApps.map { "${it.appName} (${if (it.isWhitelisted) "✓" else "✗"})" }
        )
        
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        for (i in installedApps.indices) {
            listView.setItemChecked(i, installedApps[i].isWhitelisted)
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val app = installedApps[position]
            app.isWhitelisted = !app.isWhitelisted
            
            if (app.isWhitelisted) {
                whitelistedApps.add(app.packageName)
            } else {
                whitelistedApps.remove(app.packageName)
            }
            
            saveWhitelist()
            updateAdapter()
        }
    }

    private fun updateAdapter() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_multiple_choice,
            installedApps.map { "${it.appName} (${if (it.isWhitelisted) "✓" else "✗"})" }
        )
        listView.adapter = adapter
        
        for (i in installedApps.indices) {
            listView.setItemChecked(i, installedApps[i].isWhitelisted)
        }
    }

    private fun saveWhitelist() {
        prefs.edit()
            .putStringSet("whitelisted_apps", whitelistedApps)
            .apply()
    }

    private fun checkAccessibilityPermission() {
        if (!isAccessibilityServiceEnabled()) {
            Toast.makeText(
                this,
                "Veuillez activer le service d'accessibilité pour la whitelist",
                Toast.LENGTH_LONG
            ).show()
            
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityEnabled = Settings.Secure.getInt(
            contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED, 0
        )
        
        if (accessibilityEnabled == 1) {
            val services = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            return services?.contains("${packageName}/.AppWhitelistService") == true
        }
        return false
    }
}