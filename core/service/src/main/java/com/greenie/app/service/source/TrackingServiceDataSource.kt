package com.greenie.app.service.source

import com.greenie.app.core.model.TrackingServiceData
import com.greenie.app.service.service.TrackingForegroundService
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class TrackingServiceDataSource @Inject constructor() {
    fun getTrackingServiceDataFlow(): SharedFlow<TrackingServiceData> {
        return TrackingForegroundService.trackingServiceDataSharedFlow
    }
}