package com.greenie.app.core.model

data class RecordServiceData(
    val fileName: String,
    val isRecording: Boolean,
    val isSaving: Boolean,
    val hasRecord: Boolean,
    val decibelValue: Float,
    val minimumDecibel: Float,
    val maximumDecibel: Float,
    val averageDecibel: Float,
)