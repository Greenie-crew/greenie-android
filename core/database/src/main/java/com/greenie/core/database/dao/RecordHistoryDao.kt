package com.greenie.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.greenie.core.database.model.RecordHistoryResource
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordHistoryDao {
    @Query("SELECT * FROM record_history")
    fun getAll(): Flow<List<RecordHistoryResource>>

    @Query("SELECT * FROM record_history WHERE id IN (:recordIds)")
    suspend fun getAllByIds(recordIds: IntArray): List<RecordHistoryResource>

    @Query("SELECT * FROM record_history WHERE created_at BETWEEN :startDate AND :endDate")
    suspend fun getAllByDateRange(startDate: Long, endDate: Long): List<RecordHistoryResource>

    @Query("SELECT * FROM record_history WHERE id = :recordId LIMIT 1")
    suspend fun findById(recordId: Int): RecordHistoryResource

    @Query("SELECT * FROM record_history WHERE file_name LIKE :fileName LIMIT 1")
    suspend fun findByFileName(fileName: String): RecordHistoryResource

    @Query("SELECT * FROM record_history WHERE file_path LIKE :filePath LIMIT 1")
    suspend fun findByFilePath(filePath: String): RecordHistoryResource

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg records: RecordHistoryResource): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: RecordHistoryResource): Long

    @Delete
    suspend fun delete(record: RecordHistoryResource)

    @Query("DELETE FROM record_history WHERE id = :recordId")
    suspend fun deleteById(recordId: Int)

    @Query("DELETE FROM record_history WHERE file_name LIKE :fileName")
    suspend fun deleteByFileName(fileName: String)

    @Query("DELETE FROM record_history WHERE file_path LIKE :filePath")
    suspend fun deleteByFilePath(filePath: String)
}