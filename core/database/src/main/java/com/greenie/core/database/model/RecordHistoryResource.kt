package com.greenie.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.greenie.app.core.model.RecordHistoryData

@Entity(
    tableName = "record_history",
)
data class RecordHistoryResource(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "file_name") val fileName: String,
    @ColumnInfo(name = "file_path") val filePath: String,
    @ColumnInfo(name = "duration") val duration: Long,
    @ColumnInfo(name = "minimum_decibel") val minimumDecibel: Float,
    @ColumnInfo(name = "maximum_decibel") val maximumDecibel: Float,
    @ColumnInfo(name = "average_decibel") val averageDecibel: Float,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)

fun RecordHistoryResource.asExternalModel() = RecordHistoryData(
    id = id,
    fileName = fileName,
    filePath = filePath,
    duration = duration,
    minimumDecibel = minimumDecibel,
    maximumDecibel = maximumDecibel,
    averageDecibel = averageDecibel,
    createdAt = createdAt,
)

fun RecordHistoryData.asInternalModel() = RecordHistoryResource(
    id = id,
    fileName = fileName,
    filePath = filePath,
    duration = duration,
    minimumDecibel = minimumDecibel,
    maximumDecibel = maximumDecibel,
    averageDecibel = averageDecibel,
    createdAt = createdAt,
)