package com.greenie.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.greenie.app.core.model.RecordAnalyzeData
import com.greenie.app.core.model.RecordHistoryData
import com.greenie.core.database.model.RecordHistoryResource

@Dao
interface RecordHistoryDao {
    @Query("""SELECT * FROM record_history
        WHERE created_at BETWEEN :startDate AND :endDate
        ORDER BY created_at ASC
    """)
    suspend fun getHistoryByDateRange(startDate: Long, endDate: Long): List<RecordHistoryResource>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(vararg records: RecordHistoryResource): List<Long>

    @Query("UPDATE record_history SET analyze_score = :recordAnalyzeData WHERE fileName = :fileName")
    suspend fun updateAnalyze(fileName: String, recordAnalyzeData: RecordAnalyzeData)

    @Query("DELETE FROM record_history WHERE fileName = :fileName")
    suspend fun deleteHistoryByFileName(fileName: String)
}