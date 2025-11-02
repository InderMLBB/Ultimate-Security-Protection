package com.perisaichat.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "threat_logs")
data class ThreatLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val timestamp: Long,
    
    val appSource: String,
    
    val senderId: String?,
    
    val messageSnippet: String,
    
    val reason: String,
    
    val score: Int,
    
    val hash: String,
    
    val isFalsePositive: Boolean = false,
    
    val screenshotPath: String? = null
)
