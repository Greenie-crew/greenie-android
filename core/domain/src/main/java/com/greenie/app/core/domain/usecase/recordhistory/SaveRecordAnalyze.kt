package com.greenie.app.core.domain.usecase.recordhistory

import com.greenie.app.core.domain.repository.RecordHistoryRepo
import com.greenie.app.core.model.RecordAnalyzeData
import javax.inject.Inject

class SaveRecordAnalyze @Inject constructor(
    private val recordHistoryRepo: RecordHistoryRepo
) {
    suspend operator fun invoke(fileName: String, recordAnalyzeData: RecordAnalyzeData) {
        recordHistoryRepo.saveRecordAnalyze(fileName, recordAnalyzeData)
    }
}