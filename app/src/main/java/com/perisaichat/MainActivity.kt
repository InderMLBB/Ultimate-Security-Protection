package com.perisaichat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.perisaichat.ui.ExportActivity
import com.perisaichat.ui.LogActivity
import com.perisaichat.ui.MainViewModel
import com.perisaichat.ui.SettingsActivity

class MainActivity : AppCompatActivity() {
    
    private val viewModel: MainViewModel by viewModels()
    private lateinit var shieldSwitch: Switch
    private lateinit var statusText: TextView
    private lateinit var statsText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupListeners()
        checkNotificationPermission()
        observeStats()
    }
    
    override fun onResume() {
        super.onResume()
        updateShieldStatus()
    }
    
    private fun initViews() {
        shieldSwitch = findViewById(R.id.shieldSwitch)
        statusText = findViewById(R.id.statusText)
        statsText = findViewById(R.id.statsText)
        
        findViewById<Button>(R.id.btnViewLogs).setOnClickListener {
            startActivity(Intent(this, LogActivity::class.java))
        }
        
        findViewById<Button>(R.id.btnExport).setOnClickListener {
            startActivity(Intent(this, ExportActivity::class.java))
        }
        
        findViewById<Button>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        findViewById<Button>(R.id.btnEnableAccess).setOnClickListener {
            openNotificationSettings()
        }
    }
    
    private fun setupListeners() {
        shieldSwitch.setOnCheckedChangeListener { _, isChecked ->
            val prefs = getSharedPreferences("perisai_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("shield_enabled", isChecked).apply()
            updateShieldStatus()
        }
    }
    
    private fun updateShieldStatus() {
        val prefs = getSharedPreferences("perisai_prefs", Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean("shield_enabled", true)
        
        shieldSwitch.isChecked = isEnabled
        
        val hasPermission = isNotificationServiceEnabled()
        
        statusText.text = when {
            !hasPermission -> "⚠️ Akses Notifikasi Belum Diaktifkan"
            isEnabled -> "🛡️ Perisai Aktif - Anda Terlindungi"
            else -> "⏸️ Perisai Nonaktif"
        }
    }
    
    private fun observeStats() {
        viewModel.threatCount.observe(this) { count ->
            updateStatsDisplay(count)
        }
        
        viewModel.virtexCount.observe(this) { _ ->
            viewModel.threatCount.value?.let { updateStatsDisplay(it) }
        }
        
        viewModel.phishingCount.observe(this) { _ ->
            viewModel.threatCount.value?.let { updateStatsDisplay(it) }
        }
        
        viewModel.spamCount.observe(this) { _ ->
            viewModel.threatCount.value?.let { updateStatsDisplay(it) }
        }
        
        viewModel.falsePositiveCount.observe(this) { _ ->
            viewModel.threatCount.value?.let { updateStatsDisplay(it) }
        }
    }
    
    private fun updateStatsDisplay(totalCount: Int) {
        val virtexCount = viewModel.virtexCount.value ?: 0
        val phishingCount = viewModel.phishingCount.value ?: 0
        val spamCount = viewModel.spamCount.value ?: 0
        val falsePositives = viewModel.falsePositiveCount.value ?: 0
        
        statsText.text = """
            📊 Statistik Perlindungan
            
            Total Ancaman Diblokir: $totalCount
            - Virtex: $virtexCount
            - Phishing: $phishingCount
            - Spam: $spamCount
            
            False Positives: $falsePositives
        """.trimIndent()
    }
    
    private fun checkNotificationPermission() {
        if (!isNotificationServiceEnabled()) {
            showPermissionDialog()
        }
    }
    
    private fun isNotificationServiceEnabled(): Boolean {
        val enabledListeners = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        )
        return enabledListeners?.contains(packageName) == true
    }
    
    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Akses Notifikasi Diperlukan")
            .setMessage("PerisaiChat memerlukan akses notifikasi untuk memantau dan memblokir pesan berbahaya secara real-time.\n\nAkses ini hanya digunakan untuk melindungi Anda dari virtex, phishing, dan spam.")
            .setPositiveButton("Buka Pengaturan") { _, _ ->
                openNotificationSettings()
            }
            .setNegativeButton("Nanti") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun openNotificationSettings() {
        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }
}
