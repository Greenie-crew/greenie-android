package com.greenie.app.core.data.repository

import com.greenie.app.core.data.mapper.RecordMapper.toData
import com.greenie.app.core.data.mapper.RecordMapper.toDomain
import com.greenie.app.core.domain.entities.RecordHistoryEntity
import com.greenie.app.core.domain.repository.RecordHistoryRepo
import com.greenie.app.core.model.RecordAnalyzeData
import com.greenie.app.core.model.RecordServiceData
import com.greenie.core.database.dao.RecordHistoryDao
import com.greenie.core.database.model.toRecordHistoryResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar
import javax.inject.Inject

class RecordHistoryRepoImpl @Inject constructor(
    private val recordHistoryDao: RecordHistoryDao
) : RecordHistoryRepo {
//    override fun getRecordHistoryList(): Flow<List<RecordHistoryData>> {
//        return recordHistoryDao.getHistory()
//            .map { resourceList ->
//                resourceList.map(RecordHistoryResource::asExternalModel)
//            }
//    }

    override fun getRecordHistoryByDate(
        year: Int,
        month: Int
    ): Flow<List<RecordHistoryEntity>> =
        flow {
            val startDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, 1)
            }.timeInMillis

            val endDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            }.timeInMillis

            val data = recordHistoryDao.getHistoryByDateRange(
                startDate = startDate,
                endDate = endDate
            )

            emit(data.map { resource ->
                resource.toDomain()
            })
        }

    override fun getRecordHistoryByFileName(fileName: String): Flow<RecordHistoryEntity> = flow {
        emit(recordHistoryDao.getHistoryByFileName(fileName).toDomain())
    }

    override suspend fun removeRecordHistoryByFileName(fileName: String) {
        recordHistoryDao.deleteHistoryByFileName(fileName)
    }

    override suspend fun saveRecordHistory(vararg recordServiceData: RecordServiceData) {
        recordHistoryDao.insertHistory(*recordServiceData
            .map { recordServiceItem ->
                recordServiceItem.toData().toRecordHistoryResource()
            }
            .toTypedArray()
        )
    }

    override suspend fun saveRecordAnalyze(fileName: String, recordAnalyzeData: RecordAnalyzeData) {
        recordHistoryDao.updateAnalyze(
            fileName = fileName,
            recordAnalyzeData = recordAnalyzeData
        )
    }
}