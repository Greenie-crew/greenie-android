package com.greenie.app.core.data.repository

import com.greenie.app.core.data.mapper.ServiceMapper.toDomain
import com.greenie.app.core.domain.entities.RecordServiceStateEntity
import com.greenie.app.core.domain.repository.RecordServiceRepo
import com.greenie.app.service.source.RecordServiceDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecordServiceRepoImpl @Inject constructor(
    private val recordServiceDataSource: RecordServiceDataSource,
) : RecordServiceRepo {

    override fun getRecordServiceState(): Flow<RecordServiceStateEntity> {
        return recordServiceDataSource.getRecordServiceDataFlow()
            .map {
                it.toDomain()
            }
    }
}