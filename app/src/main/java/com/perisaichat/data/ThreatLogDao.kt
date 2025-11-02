package com.perisaichat.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ThreatLogDao {
    @Query("SELECT * FROM threat_logs ORDER BY timestamp DESC")
    fun getAllLogs(): LiveData<List<ThreatLog>>
    
    @Query("SELECT * FROM threat_logs WHERE isFalsePositive = 0 ORDER BY timestamp DESC")
    fun getValidThreats(): LiveData<List<ThreatLog>>
    
    @Query("SELECT * FROM threat_logs WHERE reason = :reason ORDER BY timestamp DESC")
    fun getLogsByReason(reason: String): LiveData<List<ThreatLog>>
    
    @Query("SELECT COUNT(*) FROM threat_logs WHERE isFalsePositive = 0")
    fun getThreatCount(): LiveData<Int>
    
    @Query("SELECT COUNT(*) FROM threat_logs WHERE reason = :reason AND isFalsePositive = 0")
    fun getThreatCountByReason(reason: String): LiveData<Int>
    
    @Query("SELECT COUNT(*) FROM threat_logs WHERE isFalsePositive = 1")
    fun getFalsePositiveCount(): LiveData<Int>
    
    @Insert
    suspend fun insert(log: ThreatLog): Long
    
    @Update
    suspend fun update(log: ThreatLog)
    
    @Delete
    suspend fun delete(log: ThreatLog)
    
    @Query("DELETE FROM threat_logs")
    suspend fun deleteAll()
    
    @Query("SELECT * FROM threat_logs")
    suspend fun getAllLogsSync(): List<ThreatLog>
}
