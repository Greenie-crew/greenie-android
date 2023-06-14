package com.greenie.app.feature.tracking.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.greenie.app.feature.tracking.R
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.chart.decoration.ThresholdLine

@Composable
internal fun rememberThresholdLine(thresholdValue: Float): ThresholdLine {
    val line = shapeComponent(strokeWidth = 1.dp, strokeColor = Color.Magenta)
    val label = textComponent(
        Color.Black,
        padding = dimensionsOf(horizontal = 0.dp)
    )
    val labelText = stringResource(id = R.string.decibel_mark, ThresholdValue.toInt())
    return remember(line, label) {
        ThresholdLine(
            thresholdValue = thresholdValue,
            thresholdLabel = labelText,
            lineComponent = line,
            labelComponent = label,
        )
    }
}