package com.greenie.app.feature.deepmediresult

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenie.app.core.domain.entities.deepmedi.DeepmediHealthResultEntity
import com.greenie.app.core.domain.model.ApiResult
import com.greenie.app.core.domain.usecase.deepmedi.GetHealthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DeepmediResultViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    getHealthResult: GetHealthResult,
) : ViewModel() {
    val healthResultState: StateFlow<HealthResultUiState> =
        getHealthResult()
            .map { apiResult ->
                when(apiResult) {
                    is ApiResult.Success-> {
                        HealthResultUiState.SUCCESS(apiResult.value)
                    }
                    is ApiResult.Error -> {
                        HealthResultUiState.ERROR
                    }
                    is ApiResult.Exception -> {
                        HealthResultUiState.ERROR
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = HealthResultUiState.LOADING
            )

    sealed interface HealthResultUiState {
        data class SUCCESS(val result: DeepmediHealthResultEntity) : HealthResultUiState
        object LOADING : HealthResultUiState
        object ERROR : HealthResultUiState
    }
}