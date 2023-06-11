package com.greenie.app.core.domain.usecase.service

import com.greenie.app.core.domain.repository.RecordServiceRepo
import com.greenie.app.core.domain.repository.TrackingServiceRepo
import com.greenie.app.core.model.isRunning
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetServicesState @Inject constructor(
    private val recordServiceRepo: RecordServiceRepo,
    private val trackingServiceRepo: TrackingServiceRepo
) {
    operator fun invoke(): Flow<ServiceState> = channelFlow{
        val recordState = MutableStateFlow(false)
        val trackingState = MutableStateFlow(false)

        launch {
            combine(recordState, trackingState) { record, tracking ->
                if (record) {
                    ServiceState.Recording
                } else if (tracking) {
                    ServiceState.Tracking
                } else {
                    ServiceState.Idle
                }
            }.collectLatest { serviceState ->
                send(serviceState)
            }
        }


        launch {
            recordServiceRepo.getRecordServiceState()
                .collectLatest { recordServiceState ->
                    if (recordServiceState.recordState.isRunning()) {
                        recordState.emit(true)
                    } else {
                        recordState.emit(false)
                    }
                }
        }

        launch {
            trackingServiceRepo.getTrackingServiceState()
                .collectLatest { trackingServiceState ->
                    if (trackingServiceState.trackingState.isRunning()) {
                        trackingState.emit(true)
                    } else {
                        trackingState.emit(false)
                    }
                }
        }
    }
}

sealed interface ServiceState {
    object Idle : ServiceState
    object Recording : ServiceState
    object Tracking : ServiceState
}