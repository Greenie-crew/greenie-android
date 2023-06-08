package com.greenie.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.greenie.app.core.model.RecordAnalyzeData
import com.greenie.app.core.model.RecordHistoryData
import com.greenie.app.core.model.RecordServiceData

@Entity(
    tableName = "record_history",
)
data class RecordHistoryResource(
    @PrimaryKey(autoGenerate = false) val fileName: String,
    @ColumnInfo(name = "minimumDecimal") val minimumDecibel: Float,
    @ColumnInfo(name = "maximumDecibel") val maximumDecibel: Float,
    @ColumnInfo(name = "average_decibel") val averageDecibel: Float,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "analyze_score") val analyzeScore: RecordAnalyzeData? = null,
)

fun RecordHistoryResource.toRecordHistoryData() = RecordHistoryData(
    fileName = fileName,
    minimumDecibel = minimumDecibel,
    maximumDecibel = maximumDecibel,
    averageDecibel = averageDecibel,
    createdAt = createdAt,
)

fun RecordHistoryData.toRecordHistoryResource() = RecordHistoryResource(
    fileName = fileName,
    minimumDecibel = minimumDecibel,
    maximumDecibel = maximumDecibel,
    averageDecibel = averageDecibel,
    createdAt = createdAt,
    analyzeScore = null,
)