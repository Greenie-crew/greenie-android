package com.greenie.app.feature.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenie.app.core.domain.usecase.recordhistory.GetRecordAnalyze
import com.greenie.app.core.model.RecordAnalyzeData
import com.greenie.app.feature.result.navigation.ResultArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
        private val savedStateHandle: SavedStateHandle,
        private val getRecordAnalyze: GetRecordAnalyze,
) : ViewModel() {

    private val resultArgs: ResultArgs = ResultArgs(savedStateHandle)  // Record File Name

    private val fileName: String = resultArgs.fileName

    internal var resultUiState: StateFlow<ResultUiState> = getRecordAnalyze(fileName)
            .map { analyzeResultData ->
                if (analyzeResultData == null) {
                    return@map ResultUiState.ERROR
                }
                ResultUiState.LOADED(analyzeResultData)
            }
            .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = ResultUiState.LOADING
            )
}

sealed interface ResultUiState {
    object LOADING : ResultUiState
    object ERROR : ResultUiState
    data class LOADED(val analyzeResultData: RecordAnalyzeData) : ResultUiState
}