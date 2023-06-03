package com.greenie.app.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greenie.app.core.designsystem.theme.AppTheme

@Composable
fun PermissionDeniedDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onGoToAppSettings: () -> Unit,
    onConfirmClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.permission_title)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = permissionTextProvider.getDescription(isPermanentlyDeclined),
                    minLines = 2,
                    maxLines = 2,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isPermanentlyDeclined) {
                        onGoToAppSettings()
                    } else {
                        onConfirmClick()
                    }
                },
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.permission_confirm)
                )
            }
        },
    )
}

@Preview
@Composable
internal fun PermissionDeniedDialogPreview() {
    AppTheme {
        PermissionDeniedDialog(
            permissionTextProvider = object : PermissionTextProvider {
                @Composable
                override fun getDescription(isPermanentlyDeclined: Boolean): String {
                    return stringResource(R.string.permission_general_message)
                }
            },
            isPermanentlyDeclined = false,
            onGoToAppSettings = {},
            onConfirmClick = {},
            onDismiss = {},
        )
    }
}

interface PermissionTextProvider {
    @Composable
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

object RecordPermissionTextProvider : PermissionTextProvider {
    @Composable
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            stringResource(R.string.permission_record_message_permanently_declined)
        } else {
            stringResource(R.string.permission_record_message)
        }
    }
}

object GeneralPermissionTextProvider : PermissionTextProvider {
    @Composable
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            stringResource(R.string.permission_general_message_permanently_declined)
        } else {
            stringResource(R.string.permission_general_message)
        }
    }
}