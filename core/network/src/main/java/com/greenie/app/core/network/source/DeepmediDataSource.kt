package com.greenie.app.core.network.source

import com.greenie.app.core.network.api.DeepmediService
import com.greenie.app.core.network.model.DeepmediUploadImageResponse
import com.greenie.app.core.network.utils.toMultiPartBodyPart
import java.io.File
import javax.inject.Inject

class DeepmediDataSource @Inject constructor(
    private val deepmediService: DeepmediService
) {
    suspend fun uploadImageToDeepmedi(imageFile: File): DeepmediUploadImageResponse {
        val imageBody = imageFile.toMultiPartBodyPart("file", "image/*")
        return deepmediService.getDeepmediUploadImageResponse(imageBody)
    }

    suspend fun getHealthResult() =
        deepmediService.getDeepmediResultResponse()
}