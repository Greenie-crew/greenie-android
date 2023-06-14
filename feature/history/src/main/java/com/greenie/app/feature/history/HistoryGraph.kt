package com.greenie.app.feature.history

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greenie.app.core.designsystem.theme.Colors
import com.greenie.app.core.domain.entities.RecordHistoryEntity
import com.greenie.app.core.model.NoiseCategoryEnum
import com.greenie.app.core.model.RecordAnalyzeData
import com.greenie.app.core.model.RecordHistoryData
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shape.roundedCornerShape
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.DefaultPointConnector
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.composed.plus
import java.util.Calendar

@Composable
internal fun HistoryGraph(
    resultData: List<RecordHistoryEntity>,
//    scrollTarget: Float = 0f,
) {
    val context = LocalContext.current

    val groupedMap = remember(resultData) {
        resultData.groupBy(
            { entity ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = entity.baseInfo.createdAt
                calendar.get(Calendar.DAY_OF_MONTH)
            }, { entity ->
                entity.baseInfo
            }
        )
    }

    val maxChartEntryModelProducer = remember(groupedMap) {
        ChartEntryModelProducer(
            groupedMap.map { (day, historyData) ->
                FloatEntry(day.toFloat(), historyData.map { it.maximumDecibel }.average().toFloat())
            }
        )
    }

    val averageChartEntryModelProducer = remember(resultData) {
        ChartEntryModelProducer(
            groupedMap.map { (day, historyData) ->
                FloatEntry(day.toFloat(), historyData.map { it.averageDecibel }.average().toFloat())
            }
        )
    }

    val composedChartEntryModelProducer =
        remember(resultData) { maxChartEntryModelProducer + averageChartEntryModelProducer }

    val chartScrollState = rememberChartScrollState()

//    LaunchedEffect(key1 = scrollTarget) {
//        val maxValue = chartScrollState.maxValue
//        val currentValue = chartScrollState.value
//        val targetValue = maxValue * scrollTarget - currentValue
//        chartScrollState.animateScrollBy(targetValue)
//    }

    val startAxisValueFormatter = AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
        if (value == 0f) return@AxisValueFormatter ""
        context.getString(R.string.history_item_decibel_integer, value.toInt())
    }
    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { day, _ ->
        context.getString(R.string.history_category_detail_day, day.toInt())
    }

    val columnSpec = listOf(
        lineComponent(
            Colors.colour_5,
            thickness = 8.dp,
            shape = Shapes.roundedCornerShape(topLeft = 8.dp, topRight = 8.dp),
        )
    )


    val columnChart = columnChart(
        columns = columnSpec,
    )

    val lineSpec = remember {
        listOf(
            lineSpec(
                lineColor = Colors.fuschia_100,
                lineBackgroundShader = verticalGradient(
                    arrayOf(Colors.fuschia_100.copy(0.5f), Colors.fuschia_100.copy(alpha = 0f)),
                ),
                pointConnector = DefaultPointConnector(
                    cubicStrength = 0f
                ),
                lineThickness = 1.dp
            )
        )
    }

    val lineChart = lineChart(
        lines = lineSpec,
        axisValuesOverrider = AxisValuesOverrider.fixed(
            minY = 0f,
            maxY = 120f,
        )
    )
    Chart(
        modifier = Modifier
            .padding(horizontal = 17.dp)
            .fillMaxWidth()
            .height(200.dp),
        chart = remember(columnChart, lineChart) {
            columnChart + lineChart
        },
        chartModelProducer = composedChartEntryModelProducer,
        startAxis = startAxis(
            maxLabelCount = 7,
            valueFormatter = startAxisValueFormatter,
        ),
        bottomAxis = bottomAxis(
            valueFormatter = bottomAxisValueFormatter,
        ),
        chartScrollState = chartScrollState,
        chartScrollSpec = rememberChartScrollSpec(
            isScrollEnabled = true,
        ),
    )
}

@Preview
@Composable
internal fun HistoryGraphPreview() {
    HistoryGraph(
        resultData = listOf(
            RecordHistoryEntity(
                baseInfo = RecordHistoryData(
                    fileName = "2020-11-11.wav",
                    maximumDecibel = 100f,
                    minimumDecibel = 10f,
                    averageDecibel = 50f,
                    createdAt = Calendar.getInstance().time.time,
                ),
                analyzeScore = RecordAnalyzeData(
                    mapOf(
                        NoiseCategoryEnum.ANIMAL to 1,
                        NoiseCategoryEnum.VEHICLE to 2,
                    )
                )
            ),
            RecordHistoryEntity(
                baseInfo = RecordHistoryData(
                    fileName = "2020-11-15.wav",
                    maximumDecibel = 80f,
                    minimumDecibel = 10f,
                    averageDecibel = 50f,
                    createdAt = Calendar.getInstance().time.time,
                ),
                analyzeScore = RecordAnalyzeData(
                    mapOf(
                        NoiseCategoryEnum.ANIMAL to 5,
                        NoiseCategoryEnum.VEHICLE to 2,
                    )
                )
            ),
            RecordHistoryEntity(
                baseInfo = RecordHistoryData(
                    fileName = "2020-11-16.wav",
                    maximumDecibel = 50f,
                    minimumDecibel = 10f,
                    averageDecibel = 30f,
                    createdAt = Calendar.getInstance().time.time,
                ),
                analyzeScore = RecordAnalyzeData(
                    mapOf(
                        NoiseCategoryEnum.ANIMAL to 1,
                        NoiseCategoryEnum.VEHICLE to 2,
                    )
                )
            ),
        )
    )
}