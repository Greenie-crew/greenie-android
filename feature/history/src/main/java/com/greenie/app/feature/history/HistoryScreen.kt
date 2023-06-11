package com.greenie.app.feature.history

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.greenie.app.core.designsystem.icon.AppIcons
import com.greenie.app.core.designsystem.theme.AppTheme
import com.greenie.app.core.designsystem.theme.Colors
import com.greenie.app.core.domain.entities.RecordHistoryEntity
import com.greenie.app.core.model.NoiseCategoryEnum
import com.greenie.app.core.model.RecordAnalyzeData
import com.greenie.app.core.model.RecordHistoryData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun HistoryScreen(
    historyUiState: HistoryUiState,
    onClickPlay: (RecordHistoryEntity) -> Unit,
    onClickShowAnalyze: (RecordHistoryEntity) -> Unit,
    currentPlayingFileName: String?,
    getHistoryByDate: (year: Int, month: Int) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState()

    val rowList = listOf(
        stringResource(id = R.string.history_category_date),
        stringResource(id = R.string.history_category_tag)
    )

    val recordHistoryDataList: List<RecordHistoryEntity> by remember(historyUiState) {
        derivedStateOf {
            if (historyUiState is HistoryUiState.Success) {
                historyUiState.historyList
            } else {
                emptyList()
            }
        }
    }

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
                            spring(stiffness = Spring.StiffnessVeryLow)
                        } else {
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
                            spring(stiffness = Spring.StiffnessMedium)
                        } else {
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
            state = pagerState,
        ) { page ->
            when (page) {
                0 -> HistoryByDateSection(
                    historyUiStateData = HistoryUiStateData(
                        currentPlayingFileName = currentPlayingFileName,
                        recordHistoryList = recordHistoryDataList
                    ),
                    onClickPlay = onClickPlay,
                    onClickShowAnalyze = onClickShowAnalyze,
                    getHistoryByDate = getHistoryByDate
                )

                1 -> HistoryByTag(
                    historyUiState = historyUiState
                )
            }
        }
    }
}

internal data class HistoryUiStateData(
    val currentPlayingFileName: String?,
    val recordHistoryList: List<RecordHistoryEntity>
)

@Composable
internal fun HistoryByDateSection(
    historyUiStateData: HistoryUiStateData,
    onClickPlay: (RecordHistoryEntity) -> Unit,
    onClickShowAnalyze: (RecordHistoryEntity) -> Unit,
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

    val lazyListState = rememberLazyListState()
    var graphScrollBy by remember { mutableStateOf(0f) }

    LaunchedEffect(lazyListState.firstVisibleItemIndex) {
        delay(100L)
        if (historyUiStateData.recordHistoryList.isEmpty()) {
            return@LaunchedEffect
        }
        graphScrollBy =
            lazyListState.firstVisibleItemIndex / historyUiStateData.recordHistoryList.size.toFloat()
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
            IconButton(
                modifier = Modifier,
                onClick = {
                    if (isShowNextMonth) {
                        addMonth()
                    }
                }
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    imageVector = AppIcons.ArrowForward,
                    contentDescription = "Forward",
                    tint = if (isShowNextMonth) {
                        Color(0xFF111111)
                    } else {
                        Color(0xFFE5E5EC)
                    },
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        HistoryGraph(
            resultData = historyUiStateData.recordHistoryList,
            scrollTarget = graphScrollBy,
        )
        Spacer(modifier = Modifier.height(24.dp))

        HistoryList(
            historyUiStateData = historyUiStateData,
            onClickPlay = onClickPlay,
            onClickShowAnalyze = onClickShowAnalyze,
            lazyListState = lazyListState,
        )
    }
}

@Composable
internal fun HistoryList(
    historyUiStateData: HistoryUiStateData,
    onClickPlay: (RecordHistoryEntity) -> Unit,
    onClickShowAnalyze: (RecordHistoryEntity) -> Unit,
    lazyListState: LazyListState,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = lazyListState,
    ) {
        items(
            historyUiStateData.recordHistoryList,
            key = { it.baseInfo.fileName }) { recordHistoryEntity ->
            HistoryItemByDate(
                recordHistoryEntity = recordHistoryEntity,
                isPlaying = historyUiStateData.currentPlayingFileName == recordHistoryEntity.baseInfo.fileName,
                onClickPlay = onClickPlay,
                onClickShowAnalyze = onClickShowAnalyze
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = Colors.line_light,
                thickness = 1.dp
            )
        }
    }
}

@Composable
private fun HistoryItemByDate(
    recordHistoryEntity: RecordHistoryEntity,
    isPlaying: Boolean,
    onClickPlay: (RecordHistoryEntity) -> Unit,
    onClickShowAnalyze: (RecordHistoryEntity) -> Unit,
) {
    var containerColor by remember { mutableStateOf(Color(0xFF1A93FE)) }
    var buttonTextColor by remember { mutableStateOf(Color(0xFFFFFFFF)) }
    var buttonTextId by remember { mutableStateOf(R.string.history_item_analyze_button) }

    LaunchedEffect(Unit) {
        if (recordHistoryEntity.analyzeScore == null) {
            containerColor = Color.White
            buttonTextColor = Color(0xFF1A93FE)
            buttonTextId = R.string.history_item_analyze_button
        } else {
            containerColor = Color(0xFF1A93FE)
            buttonTextColor = Color.White
            buttonTextId = R.string.history_item_result_button
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable {
                onClickShowAnalyze(recordHistoryEntity)
            }
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onClickPlay(recordHistoryEntity)
            }
        ) {
            Icon(
                modifier = Modifier
                    .size(40.dp),
                imageVector = if (isPlaying) {
                    AppIcons.Pause
                } else {
                    AppIcons.Play
                },
                contentDescription = "Play",
                tint = Color(0xFF111111),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                modifier = Modifier
                    .wrapContentSize(),
                text = recordHistoryEntity.baseInfo.fileName,
                style = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                ),
                maxLines = 1,
            )
            Row(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProvideTextStyle(
                    value = LocalTextStyle.current.copy(
                        fontSize = 12.sp,
                        color = Color(0xFF999999),
                    )
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.history_item_decibel,
                            recordHistoryEntity.baseInfo.minimumDecibel
                        ),
                        maxLines = 1,
                    )
                    Text(
                        text = stringResource(
                            id = R.string.history_item_decibel,
                            recordHistoryEntity.baseInfo.averageDecibel
                        ),
                        maxLines = 1,
                    )
                    Text(
                        text = stringResource(
                            id = R.string.history_item_decibel,
                            recordHistoryEntity.baseInfo.maximumDecibel
                        ),
                        maxLines = 1,
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .weight(0.12f)
        )
        HistoryItemAnalyzeButton(
            containerColor = containerColor,
            buttonTextColor = buttonTextColor,
            buttonTextId = buttonTextId,
            onClickShowAnalyze = {
                onClickShowAnalyze(recordHistoryEntity)
            },
        )
    }
}

@Composable
private fun HistoryItemAnalyzeButton(
    containerColor: Color,
    buttonTextColor: Color,
    buttonTextId: Int,
    onClickShowAnalyze: () -> Unit,
) {
    Button(
        onClick = onClickShowAnalyze,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFF1A93FE),
        ),
        shape = RoundedCornerShape(50f),
    ) {
        Text(
            text = stringResource(id = buttonTextId),
            style = LocalTextStyle.current.copy(
                fontSize = 14.sp,
                color = buttonTextColor,
            )
        )
    }
}

@Composable
internal fun HistoryByTag(
    historyUiState: HistoryUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

    }
}

@Preview
@Composable
internal fun HistoryByDatePreview() {
    AppTheme {
        HistoryByDateSection(
            historyUiStateData = HistoryUiStateData(
                "",
                listOf(
                    RecordHistoryEntity(
                        baseInfo = RecordHistoryData(
                            fileName = "2020-11-11.wav",
                            maximumDecibel = 100f,
                            minimumDecibel = 100f,
                            averageDecibel = 50f,
                            createdAt = Calendar.getInstance().time.time,
                        ),
                        analyzeScore = RecordAnalyzeData(
                            mapOf(
                                NoiseCategoryEnum.ANIMAL to 1,
                                NoiseCategoryEnum.VEHICLE to 2,
                            )
                        )
                    )
                )
            ),
            onClickPlay = { _ ->
                // do nothing
            },
            onClickShowAnalyze = { _ ->
                // do nothing
            },
            getHistoryByDate = { _, _ ->
                // do nothing
            }
        )
    }
}

@Preview
@Composable
internal fun HistoryItemByDatePreview() {
    AppTheme {
        HistoryItemByDate(
            recordHistoryEntity = RecordHistoryEntity(
                baseInfo = RecordHistoryData(
                    fileName = "2020-11-11.wav",
                    maximumDecibel = 100f,
                    minimumDecibel = 100f,
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
            isPlaying = false,
            onClickPlay = {},
            onClickShowAnalyze = {}
        )
    }
}