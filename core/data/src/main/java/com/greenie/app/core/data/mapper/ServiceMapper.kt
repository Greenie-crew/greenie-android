package com.greenie.app.core.data.mapper

import com.greenie.app.core.domain.entities.RecordServiceStateEntity
import com.greenie.app.core.model.RecordServiceData

object ServiceMapper {

    fun RecordServiceData.toDomain(): RecordServiceStateEntity {
        return RecordServiceStateEntity(
            recordState = this
        )
    }
}