package com.greenie.app.feature.tracking.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.greenie.app.core.designsystem.icon.AppIcons
import com.greenie.app.core.designsystem.theme.AppTheme
import com.greenie.app.core.designsystem.theme.Colors
import com.greenie.app.feature.tracking.R

@Composable
fun TrackingRunningDialog(
    timeText: String,
    onDismiss: () -> Unit,
    isRunning: Boolean,
    onClickStart: () -> Unit,
    onClickPause: () -> Unit,
    onClickStop: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth(0.82f)
                .wrapContentHeight()
                .clip(
                    RoundedCornerShape(8.dp),
                )
                .background(
                    color = Color.White,
                ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 16.dp,
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            shape = RoundedCornerShape(8.dp),
        ) {
            IconButton(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.End),
                onClick = onDismiss
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Close",
                )
            }
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = timeText,
                style = LocalTextStyle.current.copy(
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 90.sp,
                ),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.CenterHorizontally),
            ) {
                IconButton(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(
                            RoundedCornerShape(50),
                        )
                        .background(
                            Colors.bg,
                        ),
                    onClick = onClickStop,
                ) {
                    Text(
                        text = stringResource(id = R.string.tracking_stop),
                        style = LocalTextStyle.current.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Colors.body,
                        ),
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(
                            RoundedCornerShape(50),
                        )
                        .background(
                            Colors.main_colour,
                        ),
                    onClick = if (isRunning) {
                        onClickPause
                    } else {
                        onClickStart
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(30.dp),
                        imageVector = if (isRunning) {
                            AppIcons.Pause
                        } else {
                            AppIcons.Play
                        },
                        contentDescription = "Play",
                        tint = Color.White,
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Preview
@Composable
internal fun TrackingRunningDialogPreview() {
    AppTheme {
        TrackingRunningDialog(
            timeText = "00:00:00",
            onDismiss = {},
            isRunning = true,
            onClickStart = {},
            onClickPause = {},
            onClickStop = {},
        )
    }
}