package com.greenie.app.feature.history

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenie.app.core.domain.entities.RecordHistoryEntity
import com.greenie.app.core.domain.usecase.recordhistory.GetRecordHistoryByDate
import com.greenie.app.core.model.RecordAnalyzeData
import com.greenie.app.core.model.RecordHistoryData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getRecordHistoryByDate: GetRecordHistoryByDate,
): ViewModel() {
    private val _historyUiState: MutableStateFlow<HistoryUiState> = MutableStateFlow(HistoryUiState.Loading)
    val historyUiState: StateFlow<HistoryUiState> = _historyUiState.asStateFlow()

    fun getHistoryByDate(year: Int, month: Int) {
        viewModelScope.launch {
            _historyUiState.value = HistoryUiState.Loading
            getRecordHistoryByDate(year, month).collectLatest { recordHistoryData ->
                _historyUiState.value = HistoryUiState.Success(recordHistoryData)
                Log.d("HistoryViewModel", "getHistoryByDate: $recordHistoryData")
            }
        }
    }
}

sealed interface HistoryUiState {
    object Loading : HistoryUiState
    data class Success(val historyList: List<RecordHistoryEntity>) : HistoryUiState
}