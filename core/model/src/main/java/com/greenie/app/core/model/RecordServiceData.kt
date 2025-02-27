package com.greenie.app.core.model

data class RecordServiceData(
    val serviceState: RecordServiceState,
//    val mode: RecordServiceMode,
    val fileName: String,
    val createdTime: Long,
    val decibelValue: Float,
    val minimumDecibel: Float,
    val maximumDecibel: Float,
    val averageDecibel: Float,
)

enum class RecordServiceState {
    IDLE,
    ERROR,
    RECORDING,
    PAUSED,
    SAVING,
    SAVED,
}

//enum class RecordServiceMode {
//    GENERAL,
//    TRACKING,
//}

fun RecordServiceData.isRunning(): Boolean {
    return serviceState == RecordServiceState.RECORDING || serviceState == RecordServiceState.PAUSED || serviceState == RecordServiceState.SAVING
}