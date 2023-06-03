package com.greenie.app.feature.record

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.greenie.app.core.designsystem.theme.GreenieTypography

@Composable
internal fun RecordButtonSection(
    modifier: Modifier = Modifier,
    isRecording: Boolean,
    hasRecord: Boolean,
    onStartRecord: () -> Unit,
    onPauseRecord: () -> Unit,
    onSaveRecord: () -> Unit,
    onAnalyseRecord: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (hasRecord) {
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .background(
                        color = Color(0xFF111111),
                    )
                    .clickable {
                        onAnalyseRecord()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentSize(),
                    text = stringResource(id = R.string.record_analyze_button),
                    style = GreenieTypography.b_head_reg,
                    color = Color(0xFFFFFFFF),
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .background(
                        color = Color(0xFFF1F1F5),
                    )
                    .clickable {
                        // Do nothing
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentSize(),
                    text = stringResource(id = R.string.record_analyze_button),
                    style = GreenieTypography.b_head_reg,
                    color = Color(0xFF767676),
                )
            }
        }
        Spacer(modifier = Modifier.weight(0.6f))
        if (isRecording) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .clickable {
                        onPauseRecord()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = painterResource(id = R.drawable.ic_pause),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = "Record Button"
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .clickable {
                        onStartRecord()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = painterResource(id = R.drawable.ic_record),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = "Record Button"
                )
            }
        }
        Spacer(modifier = Modifier.weight(0.6f))
        if (hasRecord) {
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .background(
                        color = Color(0xFFF1F1F5),
                    )
                    .clickable {
                        onSaveRecord()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = painterResource(id = R.drawable.ic_save),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = "Record Button"
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .clickable {
                        // Do nothing
                    },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = painterResource(id = R.drawable.ic_save),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = "Record Button"
                )
            }
        }
    }
}