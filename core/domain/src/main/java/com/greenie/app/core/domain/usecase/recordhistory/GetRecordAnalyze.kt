package com.greenie.app.core.domain.usecase.recordhistory

import com.greenie.app.common.audioanalyze.RecordFileManager
import com.greenie.app.common.audioanalyze.TensorflowHelper
import com.greenie.app.core.domain.entities.RecordHistoryEntity
import com.greenie.app.core.domain.repository.RecordHistoryRepo
import com.greenie.app.core.model.RecordAnalyzeData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRecordAnalyze @Inject constructor(
        private val recordHistoryRepo: RecordHistoryRepo,
        private val tensorflowHelper: TensorflowHelper,
        private val recordFileManager: RecordFileManager,
) {
    operator fun invoke(fileName: String): Flow<RecordHistoryEntity?> = flow {
        var recordHistoryEntity = recordHistoryRepo.getRecordHistoryByFileName(fileName).first()
        if (recordHistoryEntity.analyzeScore == null) {
            val recordFile = recordFileManager.getRecordFile(fileName)
            if (recordFile == null) {
                recordHistoryRepo.removeRecordHistoryByFileName(fileName)
                emit(null)
                return@flow
            }
            val recordAnalyzeData = RecordAnalyzeData(tensorflowHelper.analyzeAudio(recordFile).first())
            recordHistoryRepo.saveRecordAnalyze(fileName, recordAnalyzeData)

            recordHistoryEntity.apply {
                analyzeScore = recordHistoryRepo.getRecordHistoryByFileName(fileName).first().analyzeScore
            }
        }
        emit(recordHistoryEntity)
    }
}