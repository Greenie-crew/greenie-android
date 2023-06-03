package com.greenie.app.core.network.utils

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun File.toMultiPartBodyPart(paramName: String, type: String): MultipartBody.Part {
    val contentType = type.toMediaTypeOrNull()
    val requestBody = this.asRequestBody(contentType)

    return MultipartBody.Part.createFormData(paramName, name, requestBody)
}