package com.greenie.app.feature.tracking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greenie.app.core.designsystem.theme.AppTheme
import com.greenie.app.core.designsystem.theme.Colors

@Composable
internal fun TrackingAgreementScreen(
    showMessage: (String) -> Unit,
    onClickTrackingStart: () -> Unit,
) {
    val requiredAgreementMessage = stringResource(id = R.string.tracking_require_agreement_message)

    var batteryCheckbox by remember { mutableStateOf(false) }
    var recordCheckbox by remember { mutableStateOf(false) }
    var killedCheckbox by remember { mutableStateOf(false) }
    var dailyCheckbox by remember { mutableStateOf(false) }

    val allChecked by remember {
        derivedStateOf { batteryCheckbox && recordCheckbox && killedCheckbox && dailyCheckbox }
    }

    Column {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Image(
                modifier = Modifier
                    .height(184.dp)
                    .fillMaxWidth(),
                painter = painterResource(id = R.drawable.img_tracking),
                contentDescription = "Tracking Image"
            )
            Spacer(modifier = Modifier.height(32.dp))

            TrackingAgreeItem(
                title = stringResource(id = R.string.tracking_agreement_battery),
                description = stringResource(id = R.string.tracking_agreement_battery_description),
                isChecked = batteryCheckbox,
                onChecked = {
                    batteryCheckbox = !batteryCheckbox
                },
            )
            TrackingAgreeItem(
                title = stringResource(id = R.string.tracking_agreement_record),
                description = stringResource(id = R.string.tracking_agreement_record_description),
                isChecked = recordCheckbox,
                onChecked = {
                    recordCheckbox = !recordCheckbox
                },
            )
            TrackingAgreeItem(
                title = stringResource(id = R.string.tracking_agreement_killed),
                description = stringResource(id = R.string.tracking_agreement_killed_description),
                isChecked = killedCheckbox,
                onChecked = {
                    killedCheckbox = !killedCheckbox
                },
            )
            TrackingAgreeItem(
                title = stringResource(id = R.string.tracking_agreement_daily),
                description = stringResource(id = R.string.tracking_agreement_daily_description),
                isChecked = dailyCheckbox,
                onChecked = {
                    dailyCheckbox = !dailyCheckbox
                },
            )
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable {
                if (allChecked) {
                    onClickTrackingStart()
                } else {
                    showMessage(requiredAgreementMessage)
                }
            }
            .background(
                if (allChecked) {
                    Colors.main_colour
                } else {
                    Colors.bg
                }
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.tracking_start_button_title),
                style = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (allChecked) {
                        Color.White
                    } else {
                        Colors.sub_headline
                    }
                )
            )
        }
    }
}

@Composable
private fun TrackingAgreeItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onChecked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .selectable(
                selected = isChecked,
                onClick = onChecked
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.84f)
        ) {
            Text(
                text = title,
                style = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = LocalTextStyle.current.copy(
                    fontSize = 12.sp,
                    color = Colors.body
                )
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            if (isChecked) {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    imageVector = Icons.Filled.CheckCircle,
                    tint = Color.Black,
                    contentDescription = "Checkbox"
                )
            } else {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    imageVector = Icons.Outlined.Circle,
                    contentDescription = "Checkbox"
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
internal fun TrackingScreenPreview() {
    AppTheme {
        TrackingAgreementScreen(
            showMessage = {},
            onClickTrackingStart = {}
        )
    }
}