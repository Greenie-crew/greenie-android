package com.greenie.app.feature.history

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.greenie.app.core.domain.entities.RecordHistoryEntity
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.composed.plus
import com.patrykandpatrick.vico.core.entry.entriesOf

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
    val composedChartEntryModelProducer =
        maxChartEntryModelProducer + averageChartEntryModelProducer

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
//        bottomAxis = bottomAxis(),
    )
}