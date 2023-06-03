package com.greenie.app.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeepmediResultResponse(
    val name: String,
    @SerialName("cumulant_minus_point")
    val driverPenaltyPoint: Int,
    @SerialName("profile")
    val profileImageUrl: String,
    val bpm: Int,
    val sys: Int,
    val dia: Int,
    @SerialName("resp")
    val respiratoryRate: Int,
    val fatigue: Int,
    val stress: Int,
    val temp: Double,
    val alcohol: Boolean,
    val spo2: Int
)