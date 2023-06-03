package com.greenie.app.core.network.api

import com.greenie.app.core.network.model.DeepmediUploadImageResponse
import com.greenie.app.core.network.model.DeepmediResultResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DeepmediService {
    @POST("/deepmedi-test-first")
    @Multipart
    suspend fun getDeepmediUploadImageResponse(
        @Part file: MultipartBody.Part,
    ): DeepmediUploadImageResponse

    @GET("/deepmedi-test-second")
    suspend fun getDeepmediResultResponse(
        // Nothing
    ): DeepmediResultResponse
}