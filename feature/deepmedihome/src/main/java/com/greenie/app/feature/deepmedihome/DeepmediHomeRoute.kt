package com.greenie.app.feature.deepmedihome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenie.app.feature.deepmedihome.component.PermissionDeniedScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun DeepmediHomeRoute(
    onNavigateToResult: () -> Unit,
    onExit: () -> Unit,
    showMessage: (String) -> Unit,
    viewModel: DeepmediHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.deepmediHomeState.collectAsStateWithLifecycle(DeepmediHomeState.Idle)
    var isPermissionGranted by remember { mutableStateOf(true) }

    val permissionStates = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.CAMERA,
        ),
        onPermissionsResult = { permissionResult ->
            val isDenied = permissionResult.map { permissionResultMap ->
                permissionResultMap.value
            }.contains(false)

            if (isDenied) {
                isPermissionGranted = false
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionStates.launchMultiplePermissionRequest()
    }



    if (isPermissionGranted.not()) {
        PermissionDeniedScreen(onExit)
    }

    DeepmediHomeScreen(
        uiState = uiState,
        onNavigateToResult = onNavigateToResult,
        onImageCaptured = viewModel::uploadFaceImageToDeepmedi,
        showMessage = showMessage,
    )
}