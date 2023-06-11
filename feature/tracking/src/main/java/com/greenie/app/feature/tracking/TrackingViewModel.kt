package com.greenie.app.feature.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenie.app.core.domain.usecase.service.GetTrackingServiceState
import com.greenie.app.core.model.TrackingServiceData
import com.greenie.app.core.model.isEnd
import com.greenie.app.core.model.isRunning
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val getServicesState: GetTrackingServiceState,
) : ViewModel() {
    val trackingUiState: StateFlow<TrackingUiState> = getServicesState()
        .map { serviceEntity ->
            if (serviceEntity.trackingState.isRunning()) {
                TrackingUiState.Running(serviceEntity.trackingState)
            } else if (serviceEntity.trackingState.isEnd()) {
                TrackingUiState.End(serviceEntity.trackingState)
            } else {
                TrackingUiState.Idle
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = TrackingUiState.Idle,
        )
}

sealed interface TrackingUiState {
    object Idle : TrackingUiState
    data class Running(val trackingServiceData: TrackingServiceData) : TrackingUiState
    data class End(val trackingServiceData: TrackingServiceData) : TrackingUiState
}