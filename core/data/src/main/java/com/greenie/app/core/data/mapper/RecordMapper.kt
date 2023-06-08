package com.greenie.app.core.data.mapper

import com.greenie.app.core.domain.entities.RecordHistoryEntity
import com.greenie.app.core.domain.entities.RecordServiceStateEntity
import com.greenie.app.core.model.RecordHistoryData
import com.greenie.app.core.model.RecordServiceData
import com.greenie.core.database.model.RecordHistoryResource

object RecordMapper {

    fun RecordServiceData.toDomain(): RecordServiceStateEntity {
        return RecordServiceStateEntity(
            recordState = this
        )
    }

    fun RecordServiceData.toData(): RecordHistoryData {
        return RecordHistoryData(
            fileName = fileName,
            minimumDecibel = minimumDecibel,
            maximumDecibel = maximumDecibel,
            averageDecibel = averageDecibel,
            createdAt = createdTime,
        )
    }

    fun RecordHistoryResource.toDomain(): RecordHistoryEntity =
        RecordHistoryEntity(
            baseInfo = RecordHistoryData(
                fileName = fileName,
                minimumDecibel = minimumDecibel,
                maximumDecibel = maximumDecibel,
                averageDecibel = averageDecibel,
                createdAt = createdAt
            ),
            analyzeScore = analyzeScore
        )
}