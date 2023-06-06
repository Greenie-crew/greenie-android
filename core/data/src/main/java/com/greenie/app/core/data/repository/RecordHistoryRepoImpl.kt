package com.greenie.app.core.data.repository

import com.greenie.app.core.domain.repository.RecordHistoryRepo
import com.greenie.app.core.model.RecordHistoryData
import com.greenie.core.database.dao.RecordHistoryDao
import com.greenie.core.database.model.RecordHistoryResource
import com.greenie.core.database.model.asExternalModel
import com.greenie.core.database.model.asInternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class RecordHistoryRepoImpl @Inject constructor(
    private val recordHistoryDao: RecordHistoryDao
) : RecordHistoryRepo {
    override fun getRecordHistoryList(): Flow<List<RecordHistoryData>> {
        return recordHistoryDao.getAll()
            .map { resourceList ->
                resourceList.map(RecordHistoryResource::asExternalModel)
            }
    }

    override fun getRecordHistoryById(recordId: Int): Flow<RecordHistoryData> = flow {
        emit(recordHistoryDao.findById(recordId).asExternalModel())
    }

    override fun getRecordHistoryByMonth(year: Int, month: Int): Flow<List<RecordHistoryData>> =
        flow {
            val startDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, 1)
            }.timeInMillis

            val endDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            }.timeInMillis

            val data = recordHistoryDao.getAllByDateRange(
                startDate = startDate,
                endDate = endDate
            )

            emit(data.map(RecordHistoryResource::asExternalModel))
        }

    override fun getRecordHistoryByFileName(fileName: String): Flow<RecordHistoryData> = flow {
        emit(recordHistoryDao.findByFileName(fileName).asExternalModel())
    }

    override fun saveRecordHistory(vararg recordHistoryData: RecordHistoryData) = flow {
        emit(
            recordHistoryDao.insertAll(
                *recordHistoryData.map {
                    it.asInternalModel()
                }.toTypedArray()
            )
        )
    }

}