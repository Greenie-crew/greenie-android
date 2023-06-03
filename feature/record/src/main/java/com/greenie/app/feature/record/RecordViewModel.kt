package com.greenie.app.feature.record

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenie.app.core.domain.usecase.GetRecordServiceState
import com.greenie.app.core.model.RecordServiceData
import com.greenie.app.feature.record.navigation.RecordArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getRecordServiceState: GetRecordServiceState,
) : ViewModel() {
    internal val recordArgs: RecordArgs = RecordArgs(savedStateHandle)  // Record Type

    val recordServiceData: Flow<RecordServiceData> = getRecordServiceState()
        .map {
            it.recordState
        }

    private val _recordUiState = MutableSharedFlow<RecordUiState>()
    internal val recordUiState: StateFlow<RecordUiState> = _recordUiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = RecordUiState.IDLE
        )

    init {
        viewModelScope.launch {
            recordServiceData.collectLatest { serviceData ->
                if (serviceData.isRecording) {
                    _recordUiState.emit(RecordUiState.IDLE)
                } else {
                    if (serviceData.hasRecord) {
                        _recordUiState.emit(RecordUiState.IDLE)
                    } else {
                        _recordUiState.emit(RecordUiState.LOADING)
                    }
                }
            }
        }
    }
}

internal sealed interface RecordUiState {
    object IDLE : RecordUiState
    object LOADING : RecordUiState
}