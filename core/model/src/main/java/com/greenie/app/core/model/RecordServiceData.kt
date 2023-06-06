package com.greenie.app.core.model

data class RecordServiceData(
    val fileName: String,
    val createdTime: Long,
    val serviceState: RecordServiceState,
    val decibelValue: Float,
    val minimumDecibel: Float,
    val maximumDecibel: Float,
    val averageDecibel: Float,
)

enum class RecordServiceState {
    IDLE,
    RECORDING,
    PAUSED,
    SAVING,
    SAVED,
}