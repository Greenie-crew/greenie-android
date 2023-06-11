package com.greenie.app.core.domain.usecase.recordhistory

import com.greenie.app.core.domain.entities.RecordHistoryEntity
import com.greenie.app.core.domain.repository.RecordHistoryRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecordHistoryByDate @Inject constructor(
    private val recordHistoryRepo: RecordHistoryRepo
) {
    operator fun invoke(year: Int, month: Int): Flow<List<RecordHistoryEntity>> =
        recordHistoryRepo.getRecordHistoryByDate(year, month)
}