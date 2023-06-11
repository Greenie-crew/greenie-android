package com.greenie.app.core.model

data class TrackingServiceData(
    val serviceState: TrackingServiceState,
    val leftTime: Long,
    val loudNoiseHistory: List<NoiseHistoryData>,
)

data class NoiseHistoryData(
    val time: Long,
    val decibel: Float,
)

enum class TrackingServiceState {
    IDLE,
    TRACKING,
    PAUSE,
    END,
}

fun TrackingServiceData.isRunning(): Boolean {
    return serviceState == TrackingServiceState.TRACKING || serviceState == TrackingServiceState.PAUSE
}

fun TrackingServiceData.isEnd(): Boolean {
    return serviceState == TrackingServiceState.END
}