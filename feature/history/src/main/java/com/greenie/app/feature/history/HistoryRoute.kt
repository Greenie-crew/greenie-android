package com.greenie.app.feature.history

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenie.app.core.designsystem.icon.AppIcons
import com.greenie.app.core.model.RecordHistoryData
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.composed.plus
import com.patrykandpatrick.vico.core.entry.entriesOf
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
internal fun HistoryRoute(
    showMessage: (String) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val historyUiState by viewModel.historyUiState.collectAsStateWithLifecycle()

    HistoryScreen(
        historyUiState = historyUiState,
        getHistoryByDate = { year, month ->
            viewModel.getHistoryByDate(year, month)
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun HistoryScreen(
    historyUiState: HistoryUiState,
    getHistoryByDate: (year: Int, month: Int) -> Unit,
) {
    val scope = rememberCoroutineScope()

    var pagerState = rememberPagerState()

    val rowList = listOf(
        stringResource(id = R.string.history_category_date),
        stringResource(id = R.string.history_category_tag)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        TabRow(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .fillMaxWidth()
                .clip(RoundedCornerShape(50)),
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color(0xFFF7F7FB),
            contentColor = Color(0xFFF7F7FB),
            divider = { },
            indicator = { tabPositions: List<TabPosition> ->
                val transition = updateTransition(
                    pagerState.currentPage,
                    label = "Tab indicator"
                )
                val indicatorLeft by transition.animateDp(
                    transitionSpec = {
                        if (initialState < targetState) {
                            // Indicator moves to the right.
                            // The left edge moves slower than the right edge.
                            spring(stiffness = Spring.StiffnessVeryLow)
                        } else {
                            // Indicator moves to the left.
                            // The left edge moves faster than the right edge.
                            spring(stiffness = Spring.StiffnessMedium)
                        }
                    },
                    label = "Indicator left"
                ) { page ->
                    tabPositions[page].left
                }
                val indicatorRight by transition.animateDp(
                    transitionSpec = {
                        if (initialState < targetState) {
                            // Indicator moves to the right
                            // The right edge moves faster than the left edge.
                            spring(stiffness = Spring.StiffnessMedium)
                        } else {
                            // Indicator moves to the left.
                            // The right edge moves slower than the left edge.
                            spring(stiffness = Spring.StiffnessVeryLow)
                        }
                    },
                    label = "Indicator right"
                ) { page ->
                    tabPositions[page].right
                }
                Box(
                    modifier = Modifier
                        .wrapContentSize(align = Alignment.BottomStart)
                        .offset(x = indicatorLeft)
                        .width(indicatorRight - indicatorLeft)
                        .fillMaxSize()
                        .border(
                            width = 1.dp,
                            color = Color(0xFF1A93FE),
                            shape = RoundedCornerShape(50)
                        )
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                )
            }
        ) {
            rowList.forEachIndexed { index, title ->
                Tab(
                    modifier = Modifier
                        .zIndex(2f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(50)),
                    text = {
                        Text(
                            text = title,
                            style = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = if (pagerState.currentPage == index) {
                                    Color(0xFF1A93FE)
                                } else {
                                    Color(0xFF999999)
                                }
                            )
                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        HorizontalPager(
            pageCount = rowList.size,
            state = pagerState
        ) { page ->
            when (page) {
                0 -> ResultByDate(
                    historyUiState = historyUiState,
                    getHistoryByDate = getHistoryByDate
                )

                1 -> ResultByTag(
                    historyUiState = historyUiState
                )
            }
        }
    }
}

@Composable
internal fun ColumnScope.ResultByDate(
    historyUiState: HistoryUiState,
    getHistoryByDate: (year: Int, month: Int) -> Unit,
) {
    var selectedTime by remember { mutableStateOf(Calendar.getInstance().time) }
    val year by remember(selectedTime) {
        derivedStateOf {
            val calendar = Calendar.getInstance()
            calendar.time = selectedTime
            calendar.get(Calendar.YEAR)
        }
    }
    val month by remember(selectedTime) {
        derivedStateOf {
            val calendar = Calendar.getInstance()
            calendar.time = selectedTime
            calendar.get(Calendar.MONTH) + 1
        }
    }

    val isShowNextMonth by remember(month) {
        derivedStateOf {
            val currentCalendar = Calendar.getInstance()
            currentCalendar.add(Calendar.MONTH, -1)
            selectedTime.time < currentCalendar.time.time
        }
    }

    val recordHistoryData: List<RecordHistoryData> by remember(historyUiState) {
        derivedStateOf {
            if (historyUiState is HistoryUiState.Success) {
                historyUiState.historyList
            } else {
                emptyList()
            }
        }
    }

    LaunchedEffect(selectedTime) {
        getHistoryByDate(year, month)
    }

    val addMonth = {
        val calendar = Calendar.getInstance()
        calendar.time = selectedTime
        calendar.add(Calendar.MONTH, 1)
        selectedTime = calendar.time
    }

    val minusMonth = {
        val calendar = Calendar.getInstance()
        calendar.time = selectedTime
        calendar.add(Calendar.MONTH, -1)
        selectedTime = calendar.time
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 90.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier,
                onClick = minusMonth,
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    imageVector = AppIcons.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF111111),
                )
            }
            Text(
                text = stringResource(id = R.string.history_category_detail_month, month),
                style = LocalTextStyle.current.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                )
            )
            if (isShowNextMonth) {
                IconButton(
                    modifier = Modifier,
                    onClick = addMonth,
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        imageVector = AppIcons.ArrowForward,
                        contentDescription = "Forward",
                        tint = Color(0xFF111111),
                    )
                }
            } else {
                IconButton(
                    modifier = Modifier,
                    onClick = {}
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        imageVector = AppIcons.ArrowForward,
                        contentDescription = "Forward",
                        tint = Color(0xFFE5E5EC),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        ResultContentSection(
            resultData = recordHistoryData
        )
    }
}

@Composable
private fun ResultContentSection(
    resultData: List<RecordHistoryData>,
) {
    val maxChartEntryModelProducer = ChartEntryModelProducer(
        entriesOf(
            *(resultData.map { it.maximumDecibel }.toTypedArray()),
        ),
    )
    val averageChartEntryModelProducer = ChartEntryModelProducer(
        entriesOf(
            *(resultData.map { it.averageDecibel }.toTypedArray()),
        ),
    )
    val composedChartEntryModelProducer = maxChartEntryModelProducer + averageChartEntryModelProducer

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

@Composable
internal fun ColumnScope.ResultByTag(
    historyUiState: HistoryUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

    }
}