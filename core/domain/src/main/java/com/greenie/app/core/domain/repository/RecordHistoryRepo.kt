package com.greenie.app.core.domain.repository

import com.greenie.app.core.domain.entities.RecordHistoryEntity
import com.greenie.app.core.model.RecordAnalyzeData
import com.greenie.app.core.model.RecordServiceData
import kotlinx.coroutines.flow.Flow

interface RecordHistoryRepo {
    fun getRecordHistoryByDate(year: Int, month: Int): Flow<List<RecordHistoryEntity>>

    fun getRecordHistoryByFileName(fileName: String): Flow<RecordHistoryEntity>

    suspend fun removeRecordHistoryByFileName(fileName: String)

    suspend fun saveRecordHistory(vararg recordServiceData: RecordServiceData)

    suspend fun saveRecordAnalyze(fileName: String, recordAnalyzeData: RecordAnalyzeData)
}