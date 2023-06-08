package com.greenie.app.core.model

data class RecordHistoryData(
    val fileName: String,
    val minimumDecibel: Float,
    val maximumDecibel: Float,
    val averageDecibel: Float,
    val createdAt: Long,
)