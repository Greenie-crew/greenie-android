package com.greenie.app.core.domain.usecase.service

import com.greenie.app.core.domain.entities.RecordServiceStateEntity
import com.greenie.app.core.domain.repository.RecordServiceRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecordServiceState @Inject constructor(
    private val recordServiceRepo: RecordServiceRepo
) {
    operator fun invoke(): Flow<RecordServiceStateEntity> {
        return recordServiceRepo.getRecordServiceState()
    }
}