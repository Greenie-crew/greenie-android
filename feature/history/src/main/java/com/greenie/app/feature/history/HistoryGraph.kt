package com.greenie.app.feature.history

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.composed.plus
import com.patrykandpatrick.vico.core.entry.entriesOf
import java.util.Calendar

@Composable
internal fun HistoryGraph(
    resultData: List<RecordHistoryEntity>,
) {
    val maxChartEntryModelProducer = ChartEntryModelProducer(
        entriesOf(
            *(resultData.map { it.baseInfo.maximumDecibel }.toTypedArray()),
        ),
    )
    val averageChartEntryModelProducer = ChartEntryModelProducer(
        entriesOf(
            *(resultData.map { it.baseInfo.averageDecibel }.toTypedArray()),
        ),
    )
    val composedChartEntryModelProducer = maxChartEntryModelProducer + averageChartEntryModelProducer

    val axisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { i, _ ->
        "${i.toInt()} Lorem ipsum"
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
        bottomAxis = bottomAxis(),
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
                    "",
                    30f,
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
                    "",
                    30f,
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
                    "",
                    30f,
                    mapOf(
                        NoiseCategoryEnum.ANIMAL to 1,
                        NoiseCategoryEnum.VEHICLE to 2,
                    )
                )
            ),
        )
    )
}