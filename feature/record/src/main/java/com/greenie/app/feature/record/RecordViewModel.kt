package com.greenie.app.feature.record

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenie.app.core.domain.usecase.recordhistory.SaveRecordHistory
import com.greenie.app.core.domain.usecase.service.GetRecordServiceState
import com.greenie.app.core.model.RecordServiceData
import com.greenie.app.core.model.RecordServiceState
import com.greenie.app.feature.record.navigation.RecordArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getRecordServiceState: GetRecordServiceState,
    private val saveRecordHistory: SaveRecordHistory,
) : ViewModel() {
    internal val recordArgs: RecordArgs = RecordArgs(savedStateHandle)  // Record Type

    private val _recordUiState = MutableSharedFlow<RecordUiState>()
    internal val recordUiState: StateFlow<RecordUiState> = _recordUiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = RecordUiState.IDLE
        )

    val recordServiceData: StateFlow<RecordServiceData> = getRecordServiceState()
        .map { serviceEntity ->
            when (serviceEntity.recordState.serviceState) {
                RecordServiceState.SAVING -> {
                    _recordUiState.emit(RecordUiState.LOADING)
                }
                RecordServiceState.SAVED -> {
                    saveRecordHistory(serviceEntity.recordState)
                    _recordUiState.emit(RecordUiState.SAVED)
                }
                else -> {
                    _recordUiState.emit(RecordUiState.IDLE)
                }
            }

            serviceEntity.recordState
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = RecordServiceData(
                serviceState = RecordServiceState.IDLE,
                fileName = "",
                createdTime = 0L,
                decibelValue = 0f,
                minimumDecibel = 0f,
                maximumDecibel = 0f,
                averageDecibel = 0f,
            )
        )
}

internal sealed interface RecordUiState {
    object IDLE : RecordUiState
    object LOADING : RecordUiState
    object SAVED : RecordUiState
}