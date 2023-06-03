package com.greenie.app.core.network.di

import com.greenie.app.core.network.api.DeepmediService
import com.greenie.app.core.network.retrofit.NetworkType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ServiceModule {
    @Provides
    @Singleton
    fun provideDeepmediService(
        @Named(NetworkType.DEEPMEDI) retrofit: Retrofit
    ) = retrofit.create<DeepmediService>()
}