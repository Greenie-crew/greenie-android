package com.greenie.app.feature.deepmediresult

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.greenie.app.core.designsystem.component.LoadingWheel
import com.greenie.app.core.designsystem.theme.AppTheme
import com.greenie.app.core.designsystem.theme.Colors
import com.greenie.app.core.domain.entities.deepmedi.DeepmediHealthResultEntity
import com.greenie.app.core.model.deepmedi.HealthData
import com.greenie.app.core.model.deepmedi.UserData
import com.greenie.app.feature.deepmediresult.DeepmediResultViewModel.HealthResultUiState
import com.greenie.app.feature.deepmediresult.constant.HealthMaxValue

@Composable
internal fun DeepmediResultRoute(
    onNavigateToHome: () -> Unit,
    showMessage: (String) -> Unit,
    viewModel: DeepmediResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.healthResultState.collectAsStateWithLifecycle()

    when (uiState) {
        HealthResultUiState.LOADING -> {
            LoadingWheel()
        }

        is HealthResultUiState.SUCCESS -> {
            DeepmediResultScreen(
                resultData = (uiState as HealthResultUiState.SUCCESS).result,
                onNavigateToHome = onNavigateToHome
            )
        }

        HealthResultUiState.ERROR -> {
            showMessage(stringResource(id = R.string.error_message))
        }
    }
}

@Composable
internal fun DeepmediResultScreen(
    resultData: DeepmediHealthResultEntity,
    onNavigateToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Center),
                text = stringResource(id = R.string.deepmedi_result_title),
                fontSize = 16.sp,
            )
            Text(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .clickable {
                        onNavigateToHome()
                    }
                    .wrapContentSize()
                    .align(Alignment.CenterStart),
                text = stringResource(id = R.string.deepmedi_result_back_button),
                fontSize = 16.sp,
                color = Colors.text_red
            )
        }
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Colors.divider
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            DeepmediResultUserSection(resultData.userData)
            Divider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Colors.divider,
            )
            DeepmediResultHealthSection(resultData.healthData)
        }
    }
}

@Composable
internal fun DeepmediResultUserSection(
    userData: UserData
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(id = R.string.deepmedi_result_user_info_title),
            fontSize = 14.sp,
            color = Colors.text_red,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(CircleShape),
                model = userData.profileImageUrl,
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(30.dp))
            Column(
                modifier = Modifier
                    .weight(2f)
                    .wrapContentHeight(),
            ) {
                Text(
                    text = stringResource(id = R.string.deepmedi_result_user_info_name, userData.name),
                    fontSize = 12.sp,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(id = R.string.deepmedi_result_user_info_penalty, userData.driverPenaltyPoint),
                    fontSize = 12.sp,
                )
            }
        }
        Spacer(modifier = Modifier.height(120.dp))
    }
}

private data class HealthResultItemData(
    val title: String,
    val description: String,
    val type: HealthResultType? = null,
)

private enum class HealthResultType(val stringId: Int, val backgroundColor: Color) {
    NORMAL(R.string.normal, Color(0xFF66B300)),
    WARNING(R.string.warning, Color(0xFFFFD000)),
    CAUTION(R.string.caution, Color(0xFFFF6C00)),
    DANGER(R.string.danger, Color(0xFFDF0000)),
}

@Composable
internal fun DeepmediResultHealthSection(
    healthData: HealthData
) {
    val itemDataArray = arrayOf(
        HealthResultItemData(
            title = stringResource(R.string.deepmedi_result_health_bpm_title),
            description = stringResource(R.string.deepmedi_result_health_bpm_content, healthData.bpm),
            type = getHealthResultType(healthData.bpm, HealthMaxValue.BPM),
        ),
        HealthResultItemData(
            title = stringResource(R.string.deepmedi_result_health_cardiovascular_title),
            description = stringResource(
                R.string.deepmedi_result_health_cardiovascular_content,
                healthData.sys,
                healthData.dia
            ),
            type = getCardiovascularResultType(healthData.sys, healthData.dia),
        ),
        HealthResultItemData(
            title = stringResource(R.string.deepmedi_result_health_respiration_title),
            description = stringResource(
                R.string.deepmedi_result_health_respiration_content,
                healthData.respiratoryRate
            ),
            type = getHealthResultType(healthData.respiratoryRate, HealthMaxValue.RESPIRATORY_RATE),
        ),
        HealthResultItemData(
            title = stringResource(R.string.deepmedi_result_health_fatigue_title),
            description = stringResource(
                R.string.deepmedi_result_health_fatigue_content,
                healthData.fatigue
            ),
            type = getHealthResultType(healthData.fatigue, HealthMaxValue.FATIGUE),
        ),
        HealthResultItemData(
            title = stringResource(R.string.deepmedi_result_health_stress_title),
            description = stringResource(
                R.string.deepmedi_result_health_stress_content,
                healthData.stress
            ),
            type = getHealthResultType(healthData.stress, HealthMaxValue.STRESS),
        ),
        HealthResultItemData(
            title = stringResource(R.string.deepmedi_result_health_temperature_title),
            description = stringResource(
                R.string.deepmedi_result_health_temperature_content,
                healthData.temp
            ),
        ),
        HealthResultItemData(
            title = stringResource(R.string.deepmedi_result_health_alcohol_title),
            description = if (healthData.alcohol) {
                stringResource(R.string.deepmedi_result_health_alcohol_content_true)
            } else {
                stringResource(R.string.deepmedi_result_health_alcohol_content_false)
            },
        ),
        HealthResultItemData(
            title = stringResource(R.string.deepmedi_result_health_spo2_title),
            description = stringResource(
                R.string.deepmedi_result_health_spo2_content,
                healthData.spo2
            ),
            type = HealthResultType.NORMAL,
        ),
    )

    val columnCount = remember { 4 }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(id = R.string.deepmedi_result_health_title),
            fontSize = 14.sp,
            color = Colors.text_red,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(id = R.string.deepmedi_result_health_description),
            fontSize = 12.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE7E7E7)),
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            repeat(itemDataArray.size / columnCount + 1) { rowIndex ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    repeat(columnCount) { columnIndex ->
                        val itemIndex = rowIndex * columnCount + columnIndex
                        if (itemIndex < itemDataArray.size) {
                            DeepmediResultHealthSectionItem(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                itemData = itemDataArray[itemIndex]
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeepmediResultHealthSectionItem(
    modifier: Modifier = Modifier,
    itemData: HealthResultItemData,
) {
    Column(
        modifier = modifier
            .background(Color(0xDD03843))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = itemData.title,
            fontSize = 11.sp,
            fontWeight = FontWeight(700),
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = itemData.description,
            fontSize = 11.sp,
        )
        Spacer(modifier = Modifier.height(6.dp))
        if (itemData.type != null) {
            Text(
                modifier = Modifier
                    .wrapContentSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(itemData.type.backgroundColor)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                text = stringResource(id = itemData.type.stringId),
                fontSize = 8.sp,
                color = Color.White,
            )
        }
    }
}

private fun getHealthResultType(value: Int, maxValue: HealthMaxValue): HealthResultType {
    return when {
        value <= maxValue.normal -> HealthResultType.NORMAL
        value <= maxValue.warning -> HealthResultType.WARNING
        value <= maxValue.caution -> HealthResultType.CAUTION
        else -> HealthResultType.DANGER
    }
}

private fun getCardiovascularResultType(sys: Int, dia: Int): HealthResultType {
    return when {
        sys < HealthMaxValue.SYS.normal && dia < HealthMaxValue.DIA.normal -> HealthResultType.NORMAL
        sys < HealthMaxValue.SYS.warning && dia < HealthMaxValue.DIA.warning -> HealthResultType.WARNING
        sys < HealthMaxValue.SYS.caution && dia < HealthMaxValue.DIA.caution -> HealthResultType.CAUTION
        else -> HealthResultType.DANGER
    }
}

@Preview
@Composable
internal fun DeepmediResultScreenPreview() {
    AppTheme() {
        DeepmediResultScreen(
            onNavigateToHome = {},
            resultData = DeepmediHealthResultEntity(
                userData = UserData(
                    name = "김다롱",
                    profileImageUrl = "https://blog.kakaocdn.net/dn/bcT2I5/btsakc4v0Q1/xsQnP0yiy98wJh5cQkA8E1/img.png",
                    driverPenaltyPoint = 10
                ),
                healthData = HealthData(
                    bpm = 100,
                    sys = 120,
                    dia = 80,
                    respiratoryRate = 20,
                    fatigue = 10,
                    stress = 10,
                    temp = 36.5,
                    alcohol = false,
                    spo2 = 100,
                )
            )
        )
    }
}

@Preview
@Composable
internal fun DeepmediResultUserSectionPreview() {
    AppTheme() {
        DeepmediResultUserSection(
            userData = UserData(
                name = "김다롱",
                profileImageUrl = "https://blog.kakaocdn.net/dn/bcT2I5/btsakc4v0Q1/xsQnP0yiy98wJh5cQkA8E1/img.png",
                driverPenaltyPoint = 10
            )
        )
    }
}

@Preview
@Composable
internal fun DeepmediResultHealthSectionPreview() {
    AppTheme() {
        DeepmediResultHealthSection(
            healthData = HealthData(
                bpm = 100,
                sys = 120,
                dia = 80,
                respiratoryRate = 20,
                fatigue = 10,
                stress = 10,
                temp = 36.5,
                alcohol = false,
                spo2 = 100,
            )
        )
    }
}