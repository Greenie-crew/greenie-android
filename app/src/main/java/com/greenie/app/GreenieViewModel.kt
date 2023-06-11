package com.greenie.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenie.app.core.domain.usecase.service.GetServicesState
import com.greenie.app.core.domain.usecase.service.ServiceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GreenieViewModel @Inject constructor(
    private val getServiceState: GetServicesState,
) : ViewModel() {
    val serviceState: StateFlow<ServiceState> = getServiceState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ServiceState.Idle
        )
}