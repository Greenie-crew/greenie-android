package com.greenie.app.service.source

import com.greenie.app.core.model.RecordServiceData
import com.greenie.app.service.service.RecordForegroundService
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class RecordServiceDataSource @Inject constructor() {
    fun getRecordServiceDataFlow(): SharedFlow<RecordServiceData> {
        return RecordForegroundService.recordServiceDataSharedFlow
    }
}