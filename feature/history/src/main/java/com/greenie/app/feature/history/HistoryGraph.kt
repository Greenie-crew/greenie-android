package com.greenie.app.feature.history

import android.util.Log
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greenie.app.core.domain.entities.RecordHistoryEntity
import com.greenie.app.core.model.NoiseCategoryEnum
import com.greenie.app.core.model.RecordAnalyzeData
import com.greenie.app.core.model.RecordHistoryData
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.composed.plus
import com.patrykandpatrick.vico.core.entry.entriesOf
import java.util.Calendar
import java.util.Date

@Composable
internal fun HistoryGraph(
    resultData: List<RecordHistoryEntity>,
    scrollTarget: Float = 0f,
) {
    val averageChartEntryModelProducer = remember(resultData) {
        ChartEntryModelProducer(
            entriesOf(
                *(resultData.map { it.baseInfo.averageDecibel }.toTypedArray()),
            ),
        )
    }


    val maxChartEntryModelProducer = remember(resultData) {
        ChartEntryModelProducer(
            entriesOf(
                *(resultData.map { it.baseInfo.maximumDecibel }.toTypedArray()),
            ),
        )
    }

    val composedChartEntryModelProducer =
        remember(resultData) { averageChartEntryModelProducer + maxChartEntryModelProducer }

    val chartScrollState = rememberChartScrollState()

    LaunchedEffect(key1 = scrollTarget) {
        val maxValue = chartScrollState.maxValue
        val currentValue = chartScrollState.value
        val targetValue = maxValue * scrollTarget - currentValue
        Log.d("HistoryGraph", "maxValue: $maxValue, currentValue: $currentValue, targetValue: $targetValue")
        chartScrollState.animateScrollBy(targetValue)
    }

    val axisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { i, _ ->
            if (i.toInt() >= resultData.size) return@AxisValueFormatter ""
            val calendar = Calendar.getInstance()
            val time = resultData[i.toInt()].baseInfo.createdAt
            calendar.time = Date(time)
            "${calendar.get(Calendar.DAY_OF_MONTH)}"
    }

    val columnChart = columnChart()
    val lineChart = lineChart()

    Chart(
        modifier = Modifier
            .padding(horizontal = 17.dp)
            .fillMaxWidth()
            .height(200.dp),
        chart = remember(columnChart, lineChart) {
            columnChart + lineChart
        },
        chartModelProducer = composedChartEntryModelProducer,
        bottomAxis = bottomAxis(
            tickPosition = HorizontalAxis.TickPosition.Center(offset = 0, spacing = 1),
            valueFormatter = axisValueFormatter,
        ),
        chartScrollState = chartScrollState,
        chartScrollSpec = rememberChartScrollSpec(
            isScrollEnabled = false,
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
                        NoiseCategoryEnum.ANIMAL to 1,
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