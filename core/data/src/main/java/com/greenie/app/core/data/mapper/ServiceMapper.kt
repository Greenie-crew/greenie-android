package com.greenie.app.core.data.mapper

import com.greenie.app.core.domain.entities.RecordServiceEntity
import com.greenie.app.core.model.RecordServiceData

object ServiceMapper {

    fun RecordServiceData.toDomain(): RecordServiceEntity {
        return RecordServiceEntity(
            recordState = this
        )
    }
}