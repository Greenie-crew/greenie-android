package com.greenie.app.core.domain.repository

import com.greenie.app.core.domain.entities.RecordServiceEntity
import kotlinx.coroutines.flow.Flow

interface RecordServiceRepo {
    fun getRecordServiceState(): Flow<RecordServiceEntity>
}