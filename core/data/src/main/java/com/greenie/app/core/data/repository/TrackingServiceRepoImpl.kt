package com.greenie.app.core.data.repository

import com.greenie.app.core.data.mapper.RecordMapper.toDomain
import com.greenie.app.core.domain.entities.TrackingServiceStateEntity
import com.greenie.app.core.domain.repository.TrackingServiceRepo
import com.greenie.app.service.source.TrackingServiceDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TrackingServiceRepoImpl @Inject constructor(
    private val trackingServiceDataSource: TrackingServiceDataSource,
) : TrackingServiceRepo {
    override fun getTrackingServiceState(): Flow<TrackingServiceStateEntity> {
        return trackingServiceDataSource.getTrackingServiceDataFlow()
            .map {
                it.toDomain()
            }
    }
}