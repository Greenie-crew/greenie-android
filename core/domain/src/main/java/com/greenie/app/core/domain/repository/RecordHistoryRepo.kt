package com.greenie.app.core.domain.repository

import com.greenie.app.core.model.RecordHistoryData
import kotlinx.coroutines.flow.Flow

interface RecordHistoryRepo {
    fun getRecordHistoryList(): Flow<List<RecordHistoryData>>

    fun getRecordHistoryById(recordId: Int): Flow<RecordHistoryData>

    fun getRecordHistoryByMonth(year: Int, month: Int): Flow<List<RecordHistoryData>>

    fun getRecordHistoryByFileName(fileName: String): Flow<RecordHistoryData>

    fun saveRecordHistory(vararg recordHistoryData: RecordHistoryData): Flow<List<Long>>
}