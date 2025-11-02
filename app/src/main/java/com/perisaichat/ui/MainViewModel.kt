package com.perisaichat.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.perisaichat.data.AppDatabase
import com.perisaichat.data.ThreatRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ThreatRepository
    
    val threatCount: LiveData<Int>
    val virtexCount: LiveData<Int>
    val phishingCount: LiveData<Int>
    val spamCount: LiveData<Int>
    val falsePositiveCount: LiveData<Int>
    
    init {
        val database = AppDatabase.getDatabase(application)
        repository = ThreatRepository(database.threatLogDao())
        
        threatCount = repository.threatCount
        virtexCount = repository.getThreatCountByReason("virtex")
        phishingCount = repository.getThreatCountByReason("phishing")
        spamCount = repository.getThreatCountByReason("spam")
        falsePositiveCount = repository.falsePositiveCount
    }
}
