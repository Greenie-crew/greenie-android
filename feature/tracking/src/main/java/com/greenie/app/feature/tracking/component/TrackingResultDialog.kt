package com.greenie.app.feature.tracking.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.greenie.app.core.designsystem.theme.Colors
import com.greenie.app.core.designsystem.theme.ToolbarHeightCompositionLocal
import com.greenie.app.core.model.NoiseHistoryData
import com.greenie.app.feature.tracking.R
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entriesOf
import java.util.Calendar
import java.util.Date


// TODO: Make custom graph, https://proandroiddev.com/creating-graph-in-jetpack-compose-312957b11b2
const val ThresholdValue = 80f

@Composable
fun TrackingResultDialog(
    onDismiss: () -> Unit,
    onTrackingAgain: () -> Unit,
    analyzeData: List<NoiseHistoryData>,
) {
    val context = LocalContext.current

    val thresholdMap = remember {
        analyzeData.filter { noiseData ->
            noiseData.decibel >= ThresholdValue
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ToolbarHeightCompositionLocal.current)
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = stringResource(id = R.string.tracking_result_toolbar_title),
                    style = LocalTextStyle.current.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
                IconButton(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.CenterEnd),
                    onClick = onDismiss
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                    )
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .align(Alignment.BottomCenter),
                    color = Colors.line_light,
                    thickness = 1.dp,
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Spacer(modifier = Modifier.height(36.dp))
                Text(
                    modifier = Modifier
                        .align(Alignment.Start),
                    text = analyzeData.firstOrNull()?.let { data ->
                        val calendar = Calendar.getInstance()
                        calendar.time = Date(data.time)
                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH) + 1
                        val day = calendar.get(Calendar.DAY_OF_MONTH)
                        stringResource(
                            id = R.string.tracking_result_date,
                            year, month, day
                        )
                    } ?: "-",
                    style = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp,
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                TrackingGraph(
                    resultData = analyzeData,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.tracking_result_description_start))
                        withStyle(
                            style = SpanStyle(
                                color = Colors.main_colour,
                            )
                        ) {
                            append(
                                context.getString(
                                    R.string.tracking_result_description_middle,
                                    thresholdMap.size
                                )
                            )
                        }
                        append(stringResource(id = R.string.tracking_result_description_end))
                    },
                    style = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp,
                    ),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(modifier = Modifier.height(IntrinsicSize.Min)) {
                        Column {
                            val calendar = Calendar.getInstance()
                            repeat(thresholdMap.size) {
                                val data = thresholdMap[it]
                                calendar.time = Date(data.time)
                                val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
                                    stringResource(
                                        id = R.string.am
                                    )
                                } else {
                                    stringResource(id = R.string.pm)
                                }
                                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                                val minute = calendar.get(Calendar.MINUTE)
                                Row(
                                    modifier = Modifier
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(
                                                color = Colors.main_colour,
                                                shape = CircleShape
                                            )
                                            .align(Alignment.CenterVertically)
                                    )
                                    Spacer(modifier = Modifier.width(18.dp))
                                    Text(
                                        text = buildAnnotatedString {
                                            append(
                                                context.getString(
                                                    R.string.tracking_time,
                                                    amPm,
                                                    hour,
                                                    minute
                                                )
                                            )
                                            withStyle(SpanStyle(color = Colors.body)) {
                                                append(
                                                    context.getString(
                                                        R.string.tracking_decibel,
                                                        data.decibel
                                                    )
                                                )
                                            }
                                        },
                                        style = LocalTextStyle.current.copy(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Normal,
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .padding(vertical = 20.dp)
                                .padding(start = 4.dp)
                                .width(width = 2.dp)
                                .fillMaxHeight()
                                .background(color = Colors.main_colour)
                        )
                    }
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Colors.bg
                ),
                onClick = onTrackingAgain,
                shape = RectangleShape,
            ) {
                Text(
                    text = stringResource(id = R.string.tracking_again_button_title),
                    style = LocalTextStyle.current.copy(
                        color = Colors.headline,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
        }

    }
}

@Composable
internal fun TrackingGraph(
    resultData: List<NoiseHistoryData>,
) {
    val context = LocalContext.current

    val marker = rememberMarker()
    val markerMaps = remember(resultData) {
        resultData.mapIndexedNotNull() { index, data ->
            if (data.decibel > ThresholdValue) {
                index.toFloat() to marker
            } else {
                null
            }
        }.toMap()
    }

    val chartEntryModelProducer = remember(resultData) {
        ChartEntryModelProducer(
            entriesOf(
                *(resultData.map { it.decibel }.toTypedArray())
            ),
        )
    }

    val axisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { index, value ->
        val calendar = Calendar.getInstance()
        resultData[index.toInt()].time.let { millis ->
            calendar.timeInMillis = millis
            context.getString(
                R.string.tracking_graph_axis_x,
                calendar.get(Calendar.HOUR_OF_DAY % 12),
                calendar.get(Calendar.MINUTE)
            )
        }
    }

    if (resultData.isNotEmpty()) {
        val thresholdLine = rememberThresholdLine(ThresholdValue)
        val lineSpec = lineSpec(
            lineColor = Colors.main_colour,
            lineBackgroundShader = verticalGradient(
                arrayOf(Colors.main_colour.copy(0.5f), Colors.main_colour.copy(alpha = 0f)),
            ),
        )

        Chart(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            chart = lineChart(
                persistentMarkers = markerMaps,
                lines = remember { listOf(lineSpec) },
                axisValuesOverrider = AxisValuesOverrider.fixed(maxY = 120f),
                decorations = remember(thresholdLine) { listOf(thresholdLine) }
            ),
            chartModelProducer = chartEntryModelProducer,
            bottomAxis = bottomAxis(
                tickPosition = HorizontalAxis.TickPosition.Center(offset = 1, spacing = 10),
                valueFormatter = axisValueFormatter,
                guideline = null
            ),
        )
    }
}

@Preview
@Composable
internal fun TrackingResultDialogPreview() {
    TrackingResultDialog(
        onDismiss = {},
        onTrackingAgain = {},
        analyzeData = mutableListOf<NoiseHistoryData>().apply {
            repeat(3) {
                addAll(
                    listOf(
                        NoiseHistoryData(
                            time = 1686736069887,
                            decibel = 50f,
                        ),
                        NoiseHistoryData(
                            time = 1686736129887,
                            decibel = 60f,
                        ),
                        NoiseHistoryData(
                            time = 1686736189887,
                            decibel = 93f,
                        ),
                        NoiseHistoryData(
                            time = 1686736249887,
                            decibel = 53f,
                        ),
                        NoiseHistoryData(
                            time = 1686736309887,
                            decibel = 73f,
                        ),
                    )
                )
            }
        }
    )
}
