package com.greenie.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.greenie.app.core.model.RecordHistoryData
import com.greenie.app.core.model.RecordServiceData

@Entity(
    tableName = "record_history",
)
data class RecordHistoryResource(
    @ColumnInfo(name = "file_name") val fileName: String,
    @ColumnInfo(name = "minimum_decibel") val minimumDecibel: Float,
    @ColumnInfo(name = "maximum_decibel") val maximumDecibel: Float,
    @ColumnInfo(name = "average_decibel") val averageDecibel: Float,
    @ColumnInfo(name = "created_at") val createdAt: Long,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

fun RecordHistoryResource.asExternalModel() = RecordHistoryData(
    id = id,
    fileName = fileName,
    minimumDecibel = minimumDecibel,
    maximumDecibel = maximumDecibel,
    averageDecibel = averageDecibel,
    createdAt = createdAt,
)

fun RecordServiceData.asRecordHistoryResource() = RecordHistoryResource(
    fileName = fileName,
    minimumDecibel = minimumDecibel,
    maximumDecibel = maximumDecibel,
    averageDecibel = averageDecibel,
    createdAt = createdTime,
)