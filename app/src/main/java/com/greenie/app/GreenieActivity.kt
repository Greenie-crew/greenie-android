package com.greenie.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.greenie.app.core.designsystem.theme.AppTheme
import com.greenie.app.core.ui.GeneralPermissionTextProvider
import com.greenie.app.core.ui.PermissionDeniedDialog
import com.greenie.app.core.ui.RecordPermissionTextProvider
import com.greenie.app.ui.GreenieApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GreenieActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val lifecycleOwner = LocalLifecycleOwner.current
            val permissionStates = rememberMultiplePermissionsState(
                permissions = mutableListOf<String>(
                    Manifest.permission.RECORD_AUDIO
                ).apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        add(Manifest.permission.FOREGROUND_SERVICE)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                },
            )

            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_START) {
                        permissionStates.launchMultiplePermissionRequest()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            AppTheme {
                permissionStates.permissions.forEach { permissionState ->
                    if (!permissionState.status.isGranted) {
                        PermissionDeniedDialog(
                            permissionTextProvider = when (permissionState.permission) {
                                Manifest.permission.RECORD_AUDIO -> RecordPermissionTextProvider
                                else -> GeneralPermissionTextProvider
                            },
                            isPermanentlyDeclined = ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                permissionState.permission
                            ).not(),
                            onGoToAppSettings = ::openAppSettings,
                            onConfirmClick = {
                                permissionState.launchPermissionRequest()
                            },
                            onDismiss = {
                                finish()
                            },
                        )
                    }
                }

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GreenieApp()
                }
            }
        }
    }
}

private fun Activity.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}