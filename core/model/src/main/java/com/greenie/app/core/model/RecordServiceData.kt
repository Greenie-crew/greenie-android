package com.greenie.app.core.model

data class RecordServiceData(
    val decibelValue: Float,
    val minimumDecibel: Float,
    val maximumDecibel: Float,
    val averageDecibel: Float,
    val isRecording: Boolean,
    val hasRecord: Boolean,
)