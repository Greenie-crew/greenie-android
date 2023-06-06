package com.greenie.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.greenie.core.database.dao.RecordHistoryDao
import com.greenie.core.database.model.RecordHistoryResource

@Database(
    entities = [RecordHistoryResource::class],
    version = 1,
    exportSchema = false,
)
abstract class RecordDatabase : RoomDatabase(){
    abstract fun recordDao(): RecordHistoryDao
}