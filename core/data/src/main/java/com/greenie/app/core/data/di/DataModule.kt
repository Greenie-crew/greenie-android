package com.greenie.app.core.data.di

import com.greenie.app.core.data.repository.RecordHistoryRepoImpl
import com.greenie.app.core.data.repository.RecordServiceRepoImpl
import com.greenie.app.core.domain.repository.RecordHistoryRepo
import com.greenie.app.core.domain.repository.RecordServiceRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    @Singleton
    fun bindRecordServiceRepository(
        recordServiceRepo: RecordServiceRepoImpl
    ): RecordServiceRepo

    @Binds
    @Singleton
    fun bindRecordHistoryRepository(
        recordHistoryRepo: RecordHistoryRepoImpl
    ): RecordHistoryRepo
}