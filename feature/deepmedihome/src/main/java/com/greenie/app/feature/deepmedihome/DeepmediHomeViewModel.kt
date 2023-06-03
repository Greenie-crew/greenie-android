package com.greenie.app.feature.deepmedihome

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenie.app.core.domain.model.ApiResult
import com.greenie.app.core.domain.usecase.deepmedi.UploadFaceImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DeepmediHomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val uploadFaceImage: UploadFaceImage,
): ViewModel() {
    private val _deepmediHomeState: MutableSharedFlow<DeepmediHomeState> = MutableSharedFlow()
    val deepmediHomeState: SharedFlow<DeepmediHomeState>
        get() = _deepmediHomeState

    fun uploadFaceImageToDeepmedi(uri: Uri) {
        viewModelScope.launch {
            _deepmediHomeState.emit(DeepmediHomeState.Loading)
            val imageFile = File(uri.path!!)

            uploadFaceImage(imageFile).collectLatest { apiResult ->
                when(apiResult) {
                    is ApiResult.Success -> {
                        _deepmediHomeState.emit(DeepmediHomeState.Success)
                    }
                    is ApiResult.Error -> {
                        _deepmediHomeState.emit(DeepmediHomeState.Error)
                    }
                    is ApiResult.Exception -> {
                        _deepmediHomeState.emit(DeepmediHomeState.Error)
                    }
                }
            }
        }
    }


}

sealed interface DeepmediHomeState {
    object Idle: DeepmediHomeState
    object Loading: DeepmediHomeState
    object Success: DeepmediHomeState
    object Error: DeepmediHomeState
}