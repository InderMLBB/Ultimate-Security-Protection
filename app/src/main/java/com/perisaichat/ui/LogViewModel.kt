package com.perisaichat.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.perisaichat.data.AppDatabase
import com.perisaichat.data.ThreatLog
import com.perisaichat.data.ThreatRepository
import kotlinx.coroutines.launch

class LogViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ThreatRepository
    
    val allLogs: LiveData<List<ThreatLog>>
    
    init {
        val database = AppDatabase.getDatabase(application)
        repository = ThreatRepository(database.threatLogDao())
        allLogs = repository.allLogs
    }
    
    fun updateFalsePositive(log: ThreatLog, isFalsePositive: Boolean) {
        viewModelScope.launch {
            val updatedLog = log.copy(isFalsePositive = isFalsePositive)
            repository.update(updatedLog)
        }
    }
    
    fun deleteLog(log: ThreatLog) {
        viewModelScope.launch {
            repository.delete(log)
        }
    }
    
    fun deleteAllLogs() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}
