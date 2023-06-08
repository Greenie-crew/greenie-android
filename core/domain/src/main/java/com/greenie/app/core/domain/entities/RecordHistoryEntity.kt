package com.greenie.app.core.domain.entities

import com.greenie.app.core.model.RecordAnalyzeData
import com.greenie.app.core.model.RecordHistoryData

data class RecordHistoryEntity(
    val baseInfo: RecordHistoryData,
    val analyzeScore: RecordAnalyzeData?,
)