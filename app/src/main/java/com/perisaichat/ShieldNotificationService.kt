package com.perisaichat

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.perisaichat.data.AppDatabase
import com.perisaichat.data.ThreatLog
import com.perisaichat.util.FileUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShieldNotificationService : NotificationListenerService() {
    
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private lateinit var database: AppDatabase
    
    companion object {
        private const val SHIELD_CHANNEL_ID = "perisai_shield_channel"
        private const val SHIELD_NOTIFICATION_ID = 1001
        
        private val MONITORED_APPS = listOf(
            "com.whatsapp",
            "org.telegram.messenger",
            "com.facebook.orca",
            "com.instagram.android",
            "com.twitter.android"
        )
    }
    
    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(applicationContext)
        createNotificationChannel()
    }
    
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        
        sbn?.let {
            if (!shouldMonitorApp(it.packageName)) return
            
            val notification = it.notification
            val extras = notification.extras
            
            val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
            val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
            val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: text
            
            val fullMessage = "$title $bigText".trim()
            
            if (fullMessage.isNotEmpty()) {
                analyzeAndBlock(it, fullMessage)
            }
        }
    }
    
    private fun shouldMonitorApp(packageName: String): Boolean {
        return MONITORED_APPS.contains(packageName)
    }
    
    private fun analyzeAndBlock(sbn: StatusBarNotification, message: String) {
        val prefs = getSharedPreferences("perisai_prefs", Context.MODE_PRIVATE)
        val isShieldEnabled = prefs.getBoolean("shield_enabled", true)
        
        if (!isShieldEnabled) return
        
        val result = VirusDetector.analyze(message)
        
        if (result.isThreat) {
            cancelNotification(sbn.key)
            
            val messageSnippet = message.take(250)
            val hash = FileUtils.calculateSHA256(messageSnippet)
            val category = VirusDetector.categorizeReason(result)
            
            val threatLog = ThreatLog(
                timestamp = System.currentTimeMillis(),
                appSource = sbn.packageName,
                senderId = sbn.notification.extras.getString("android.title"),
                messageSnippet = messageSnippet,
                reason = category,
                score = result.score,
                hash = hash,
                isFalsePositive = false,
                screenshotPath = null
            )
            
            serviceScope.launch {
                database.threatLogDao().insert(threatLog)
            }
            
            showBlockNotification(category, result.score)
        }
    }
    
    private fun showBlockNotification(category: String, score: Int) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val categoryText = when (category) {
            "virtex" -> "Virtex"
            "phishing" -> "Phishing"
            "spam" -> "Spam"
            else -> "Ancaman"
        }
        
        val notification = NotificationCompat.Builder(this, SHIELD_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🛡️ Pesan Berbahaya Diblokir")
            .setContentText("$categoryText terdeteksi (Score: $score)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(SHIELD_NOTIFICATION_ID, notification)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SHIELD_CHANNEL_ID,
                "PerisaiChat Shield",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for blocked threats"
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
