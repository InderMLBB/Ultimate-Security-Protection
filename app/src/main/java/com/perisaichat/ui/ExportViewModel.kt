package com.perisaichat.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.perisaichat.data.AppDatabase
import com.perisaichat.data.ThreatLog
import com.perisaichat.data.ThreatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExportViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ThreatRepository
    
    init {
        val database = AppDatabase.getDatabase(application)
        repository = ThreatRepository(database.threatLogDao())
    }
    
    suspend fun getAllLogs(): List<ThreatLog> {
        return withContext(Dispatchers.IO) {
            repository.getAllLogsSync()
        }
    }
}
