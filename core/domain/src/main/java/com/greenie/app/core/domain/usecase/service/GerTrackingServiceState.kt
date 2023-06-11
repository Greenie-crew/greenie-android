package com.greenie.app.core.domain.usecase.service

import com.greenie.app.core.domain.entities.TrackingServiceStateEntity
import com.greenie.app.core.domain.repository.TrackingServiceRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrackingServiceState @Inject constructor(
    private val trackingServiceRepo: TrackingServiceRepo
) {
    operator fun invoke(): Flow<TrackingServiceStateEntity> {
        return trackingServiceRepo.getTrackingServiceState()
    }
}