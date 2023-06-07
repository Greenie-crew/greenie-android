package com.greenie.app.core.domain.usecase.recordhistory

import com.greenie.app.core.domain.repository.RecordHistoryRepo
import com.greenie.app.core.model.RecordServiceData
import javax.inject.Inject

class SaveRecordHistory @Inject constructor(
    private val recordHistoryRepo: RecordHistoryRepo
) {
    suspend operator fun invoke(vararg recordServiceData: RecordServiceData) =
        recordHistoryRepo.saveRecordHistory(*recordServiceData)
}