package com.greenie.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.greenie.app.core.model.RecordAnalyzeData
import com.greenie.app.core.model.RecordHistoryData
import com.greenie.core.database.dao.RecordHistoryDao
import com.greenie.core.database.model.RecordHistoryResource

@Database(
    entities = [RecordHistoryResource::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(MapTypeConverter::class)
abstract class RecordDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordHistoryDao
}

object MapTypeConverter {
    @TypeConverter
    @JvmStatic
    fun fromRecordAnalyzeData(recordAnalyzeData: RecordAnalyzeData?): String? {
        if (recordAnalyzeData == null) {
            return null
        }
        return Gson().toJson(recordAnalyzeData)
    }

    @TypeConverter
    @JvmStatic
    fun toRecordAnalyzeData(recordAnalyzeData: String?): RecordAnalyzeData? {
        if (recordAnalyzeData == null) {
            return null
        }
        return Gson().fromJson(recordAnalyzeData, RecordAnalyzeData::class.java)
    }
}
