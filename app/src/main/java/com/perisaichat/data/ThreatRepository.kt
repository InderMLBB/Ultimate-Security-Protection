package com.perisaichat.data

import androidx.lifecycle.LiveData

class ThreatRepository(private val threatLogDao: ThreatLogDao) {
    
    val allLogs: LiveData<List<ThreatLog>> = threatLogDao.getAllLogs()
    
    val validThreats: LiveData<List<ThreatLog>> = threatLogDao.getValidThreats()
    
    val threatCount: LiveData<Int> = threatLogDao.getThreatCount()
    
    val falsePositiveCount: LiveData<Int> = threatLogDao.getFalsePositiveCount()
    
    fun getLogsByReason(reason: String): LiveData<List<ThreatLog>> {
        return threatLogDao.getLogsByReason(reason)
    }
    
    fun getThreatCountByReason(reason: String): LiveData<Int> {
        return threatLogDao.getThreatCountByReason(reason)
    }
    
    suspend fun insert(log: ThreatLog): Long {
        return threatLogDao.insert(log)
    }
    
    suspend fun update(log: ThreatLog) {
        threatLogDao.update(log)
    }
    
    suspend fun delete(log: ThreatLog) {
        threatLogDao.delete(log)
    }
    
    suspend fun deleteAll() {
        threatLogDao.deleteAll()
    }
    
    suspend fun getAllLogsSync(): List<ThreatLog> {
        return threatLogDao.getAllLogsSync()
    }
}
