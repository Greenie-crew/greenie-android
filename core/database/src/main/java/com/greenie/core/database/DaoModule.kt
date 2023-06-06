package com.greenie.core.database

import com.greenie.core.database.dao.RecordHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    fun providesRecordDao(
        database: RecordDatabase,
    ): RecordHistoryDao = database.recordDao()
}