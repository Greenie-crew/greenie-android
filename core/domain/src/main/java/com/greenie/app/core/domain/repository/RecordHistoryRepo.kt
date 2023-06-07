package com.greenie.app.core.domain.repository

import com.greenie.app.core.model.RecordHistoryData
import com.greenie.app.core.model.RecordServiceData
import kotlinx.coroutines.flow.Flow

interface RecordHistoryRepo {
    fun getRecordHistoryList(): Flow<List<RecordHistoryData>>

    fun getRecordHistoryById(recordId: Int): Flow<RecordHistoryData>

    fun getRecordHistoryByMonth(year: Int, month: Int): Flow<List<RecordHistoryData>>

    fun getRecordHistoryByFileName(fileName: String): Flow<RecordHistoryData>

    suspend fun saveRecordHistory(vararg recordServiceData: RecordServiceData)
}