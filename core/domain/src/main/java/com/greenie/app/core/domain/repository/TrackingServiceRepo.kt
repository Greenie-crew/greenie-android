package com.greenie.app.core.domain.repository

import com.greenie.app.core.domain.entities.TrackingServiceStateEntity
import kotlinx.coroutines.flow.Flow

interface TrackingServiceRepo {
//    fun startRecord()
//    fun stopRecord()
//    fun pauseRecord()
//    fun resumeRecord()
//    fun cancelRecord()
//    fun saveRecord()
//    fun deleteRecord()

    fun getTrackingServiceState(): Flow<TrackingServiceStateEntity>
}