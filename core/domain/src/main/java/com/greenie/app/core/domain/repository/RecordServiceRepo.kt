package com.greenie.app.core.domain.repository

import com.greenie.app.core.domain.entities.RecordServiceStateEntity
import kotlinx.coroutines.flow.Flow

interface RecordServiceRepo {
//    fun startRecord()
//    fun stopRecord()
//    fun pauseRecord()
//    fun resumeRecord()
//    fun cancelRecord()
//    fun saveRecord()
//    fun deleteRecord()

    fun getRecordServiceState(): Flow<RecordServiceStateEntity>
}