package com.greenie.app.core.network.retrofit

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    private const val BASE_URL = "http://blockchain.deep-medi.com"

    @Singleton
    @Provides
    @Named(NetworkType.DEEPMEDI)
    fun provideRetrofit(): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                @OptIn(ExperimentalSerializationApi::class)
                networkJson.asConverterFactory("application/json".toMediaType()),
            )
            .client(
                provideOkHttpClient(
                    httpLoggingInterceptor
                )
            )
            .build()

    private val networkJson: Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private fun provideOkHttpClient(vararg interceptor: Interceptor): OkHttpClient =
        OkHttpClient.Builder().run {
            interceptor.forEach { addInterceptor(it) }
            build()
        }

    private var httpLoggingInterceptor = HttpLoggingInterceptor { log ->
        Log.d("OkHttp", log)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

object NetworkType {
    const val DEEPMEDI = "deep-medi"
}