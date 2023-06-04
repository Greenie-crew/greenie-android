package com.greenie.app.feature.record

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greenie.app.core.designsystem.theme.Colors
import com.greenie.app.core.designsystem.theme.GreenieTypography
import com.greenie.app.core.designsystem.theme.TriangleShape
import com.greenie.app.core.model.RecordServiceData
import kotlin.math.roundToInt

@Composable
internal fun RecordMeterSection(
    modifier: Modifier = Modifier,
    recordServiceData: RecordServiceData,
) {
    val soundMeterValue by animateFloatAsState(
        targetValue = recordServiceData.decibelValue,
        label = "Decibel Value"
    )

    val soundMeterTextColor by animateColorAsState(
        // TODO Set the color of the decibel text based on the decibel value
        targetValue = when (recordServiceData.decibelValue.roundToInt()) {
            in 0 until 30 -> Colors.`20db_noise`
            in 30 until 40 -> Colors.`30db_noise`
            in 40 until 50 -> Colors.`40db_noise`
            in 50 until 60 -> Colors.`50db_noise`
            in 60 until 70 -> Colors.`60db_noise`
            in 70 until 80 -> Colors.`70db_noise`
            in 80 until 90 -> Colors.`80db_noise`
            in 90 until 100 -> Colors.`90db_noise`
            in 100 until 110 -> Colors.`100db_noise`
            in 110 until 120 -> Colors.`110db_noise`
            else -> Colors.`120db_noise`
        },
        label = "Decibel Text Color"
    )

    val soundMeterPointerRotation by animateFloatAsState(
        targetValue = recordServiceData.decibelValue * (180 / 120f) - 90f,
        label = "Decibel Pointer Rotation"
    )

    val soundMeterDescriptionArray = stringArrayResource(id = R.array.record_decibel_array)
    val soundMeterDescription by remember(soundMeterValue) {
        derivedStateOf {
            when (soundMeterValue.roundToInt()) {
                in 0 until 10 -> soundMeterDescriptionArray[0]
                in 10 until 20 -> soundMeterDescriptionArray[1]
                in 20 until 30 -> soundMeterDescriptionArray[2]
                in 30 until 40 -> soundMeterDescriptionArray[3]
                in 40 until 50 -> soundMeterDescriptionArray[4]
                in 50 until 60 -> soundMeterDescriptionArray[5]
                in 60 until 70 -> soundMeterDescriptionArray[6]
                in 70 until 80 -> soundMeterDescriptionArray[7]
                in 80 until 90 -> soundMeterDescriptionArray[8]
                in 90 until 100 -> soundMeterDescriptionArray[9]
                in 100 until 110 -> soundMeterDescriptionArray[10]
                in 110 until 120 -> soundMeterDescriptionArray[11]
                else -> soundMeterDescriptionArray[12]
            }
        }
    }

    Column(
        modifier = modifier,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .padding(top = 26.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                painter = painterResource(id = R.drawable.decibel_dashboard),
                contentScale = ContentScale.FillWidth,
                contentDescription = "Decibel Dashboard"
            )

            Box(
                modifier = Modifier
                    .padding(bottom = maxWidth * DecibelPointerSize / 2)
                    .fillMaxWidth(0.05f)
                    .aspectRatio(0.14f)
                    .align(alignment = Alignment.BottomCenter)
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.5f, 1.0f)
                        rotationZ = soundMeterPointerRotation
                    }
                    .background(
                        color = decibel_pointer,
                        shape = TriangleShape
                    ),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(DecibelPointerSize)
                    .aspectRatio(1f)
                    .align(alignment = Alignment.BottomCenter)
                    .background(
                        color = decibel_pointer,
                        shape = CircleShape,
                    ),
            )
        }

        Row(
            modifier = Modifier
                .padding(top = 4.dp)
                .wrapContentSize()
                .align(alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                modifier = Modifier
                    .wrapContentSize(),
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = GreenieTypography.number_60.fontSize,
                            fontWeight = GreenieTypography.number_60.fontWeight,
                            color = soundMeterTextColor,
                        )
                    ) {
                        append(String.format("%.1f", soundMeterValue).padStart(4, '0'))
                    }
                    withStyle(
                        style = SpanStyle(
                            fontSize = GreenieTypography.b_head_reg.fontSize,
                            fontWeight = GreenieTypography.b_head_reg.fontWeight,
                            color = soundMeterTextColor,
                        )
                    ) {
                        append(stringResource(id = R.string.decibel_unit))
                    }
                }
            )
        }

        Text(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 14.dp),
            text = soundMeterDescription,
            color = LocalTextStyle.current.color,
            style = GreenieTypography.a_result,
        )

        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .padding(horizontal = 16.dp)
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .background(
                    color = record_state_background,
                    shape = RectangleShape
                )
                .padding(vertical = 14.dp),
        ) {
            repeat(3) { index ->
                RecordDetailInfoItem(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    decibelValue = when (index) {
                        0 -> recordServiceData.minimumDecibel
                        1 -> recordServiceData.averageDecibel
                        else -> recordServiceData.maximumDecibel
                    },
                    description = when (index) {
                        0 -> stringResource(id = R.string.minimum_decibel_title)
                        1 -> stringResource(id = R.string.average_decibel_title)
                        else -> stringResource(id = R.string.maximum_decibel_title)
                    }
                )
                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight(),
                    thickness = 1.dp,
                    color = Colors.line_light,
                )
            }
        }

    }
}

@Composable
private fun RecordDetailInfoItem(
    modifier: Modifier = Modifier,
    decibelValue: Float,
    description: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .wrapContentSize(),
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                ) {
                    append(String.format("%.1f", decibelValue).padStart(4, '0'))
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                    )
                ) {
                    append(stringResource(id = R.string.decibel_unit))
                }
            },
            fontSize = 14.sp,
        )
        Text(
            modifier = Modifier
                .wrapContentSize(),
            text = description,
            style = GreenieTypography.b_head_reg,
            color = Colors.body,
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    heightDp = 640,
    name = "Record Meter Section"
)
@Composable
internal fun PreviewRecordMeterSection() {
    RecordMeterSection(
        recordServiceData = RecordServiceData(
            decibelValue = 50f,
            isRecording = false,
            isSaving = false,
            hasRecord = true,
            minimumDecibel = 0f,
            maximumDecibel = 120f,
            averageDecibel = 50f,
        )
    )
}

@Preview(
    showBackground = true,
    widthDp = 320,
    heightDp = 640,
    name = "Record Meter Section - Decibel Value 0"
)
@Composable
internal fun PreviewRecordDetailInfoItem() {
    RecordDetailInfoItem(
        decibelValue = 0f,
        description = "Test",
    )
}

val decibel_pointer = Color(0xFF3C3C3C)
val record_state_background = Color(0xFFF7F7FB)