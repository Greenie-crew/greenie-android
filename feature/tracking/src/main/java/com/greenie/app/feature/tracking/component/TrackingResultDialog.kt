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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.chart.decoration.ThresholdLine
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
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.tracking_result_description_start))
                        withStyle(
                            style = SpanStyle(
                                color = Colors.main_colour,
                            )
                        ) {
                            append(context.getString(R.string.tracking_result_description_middle, thresholdMap.size))
                        }
                        append(stringResource(id = R.string.tracking_result_description_end))
                    },
                    style = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp,
                    ),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    Box(modifier = Modifier
                        .width(56.dp)
                        .fillMaxHeight()
                        .align(Alignment.TopStart)
                        .background(Colors.main_colour)
                    )
                    Column {
                        repeat(thresholdMap.size) {
//                            Text(text =)
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                // TODO: Add reset button
            }
        }
    }
}

@Composable
internal fun TrackingGraph(
    resultData: List<NoiseHistoryData>,
) {
    val splitData = remember {
        resultData.chunked(10)
            .map { list ->
                NoiseHistoryData(
                    time = list.firstOrNull()?.time ?: 0,
                    decibel = list.map { it.decibel }.max(),
                )
            }
    }
    val chartEntryModelProducer = remember(splitData) {
        ChartEntryModelProducer(
            entriesOf(
                *(splitData.map { it.decibel }.toTypedArray())
            ),
        )
    }

    val axisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { i, _ ->
        val index = i.toInt()
        if (index >= splitData.size) return@AxisValueFormatter ""
        val calendar = Calendar.getInstance()
        val time = splitData[index].time.run {
            calendar.time = Date(this)
            "${calendar.get(Calendar.HOUR_OF_DAY % 12)}"
        }
        if (index >= 1) {
            val beforeTime = splitData[index - 1].time.run {
                calendar.time = Date(this)
                "${calendar.get(Calendar.HOUR_OF_DAY % 12)}"
            }
            if (time == beforeTime) {
                return@AxisValueFormatter ""
            }
        }
        String.format("%2d", time.toInt())
    }

    Chart(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        chart = lineChart(
            axisValuesOverrider = AxisValuesOverrider.fixed(
                minX = 0f,
                maxX = splitData.size.toFloat() - 1,
            )
        ).apply {
            addDecoration(
                ThresholdLine(
                    thresholdValue = ThresholdValue,
                    lineComponent = shapeComponent(color = Color.Cyan),
                    labelComponent = textComponent(
                        Color.Black,
                        padding = dimensionsOf(horizontal = 8.dp)
                    ),
                )
            )
        },
        chartModelProducer = chartEntryModelProducer,
        bottomAxis = bottomAxis(
            tickPosition = HorizontalAxis.TickPosition.Center(),
            valueFormatter = axisValueFormatter,
        ),
    )
}