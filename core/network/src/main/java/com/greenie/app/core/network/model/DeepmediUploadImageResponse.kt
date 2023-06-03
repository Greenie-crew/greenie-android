package com.greenie.app.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeepmediUploadImageResponse (
    @SerialName("code")
    val code: Int,
    @SerialName("message")
    val message: String = "",
)