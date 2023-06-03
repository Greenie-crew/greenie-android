package com.greenie.app.feature.deepmedihome

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greenie.app.feature.deepmedihome.R
import com.greenie.app.core.designsystem.theme.AppTheme
import com.greenie.app.core.designsystem.theme.Colors
import com.greenie.app.feature.deepmedihome.component.CameraPreview
import com.greenie.app.feature.deepmedihome.utils.takePhoto
import kotlinx.coroutines.delay

const val navigateToResultDelay = 1000L

@Composable
internal fun DeepmediHomeScreen(
    uiState: DeepmediHomeState,
    onNavigateToResult: () -> Unit,
    onImageCaptured: (Uri) -> Unit,
    showMessage: (String) -> Unit,
) {
    val context = LocalContext.current
    val lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder().build()
    }

    var homeDescription by remember { mutableStateOf(buildAnnotatedString {}) }
    var enableCapture by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = uiState) {
        when (uiState) {
            is DeepmediHomeState.Idle -> {
                homeDescription = buildAnnotatedString {
                    append(context.getString(R.string.deepmedi_home_description_start))
                    withStyle(style = SpanStyle(color = Colors.text_red)) {
                        append(context.getString(R.string.deepmedi_home_description_highlight))
                    }
                    append(context.getString(R.string.deepmedi_home_description_end))
                }
                enableCapture = true
            }

            is DeepmediHomeState.Loading -> {
                homeDescription = buildAnnotatedString {
                    append(context.getString(R.string.deepmedi_home_description_loading))
                }
                enableCapture = false
            }

            is DeepmediHomeState.Success -> {
                homeDescription = buildAnnotatedString {
                    append(context.getString(R.string.deepmedi_home_description_result))
                    withStyle(SpanStyle(color = Colors.text_red)) {
                        append(context.getString(R.string.deepmedi_home_description_result_success))
                    }
                }
                delay(navigateToResultDelay)
                onNavigateToResult()
            }

            is DeepmediHomeState.Error -> {
                homeDescription = buildAnnotatedString {
                    append(context.getString(R.string.deepmedi_home_description_result))
                    withStyle(SpanStyle(color = Colors.text_red)) {
                        append(context.getString(R.string.deepmedi_home_description_result_failed))
                    }
                }
                enableCapture = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            modifier = Modifier
                .padding(start = 38.dp)
                .padding(vertical = 16.dp)
                .wrapContentWidth(align = BiasAlignment.Horizontal(-0.8f)),
            text = stringResource(id = R.string.deepmedi_home_title),
            fontSize = 16.sp,
        )
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Colors.divider
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                modifier = Modifier
                    .weight(0.7f),
                text = homeDescription,
                fontSize = 16.sp,
                fontWeight = FontWeight(700),
                lineHeight = 26.sp,
            )
            Spacer(modifier = Modifier.height(60.dp))
            CameraPreview(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
                    .wrapContentHeight(),
                cameraBind = enableCapture,
                imageCapture = imageCapture,
                lensFacing = lensFacing,
            )
            Spacer(modifier = Modifier.height(24.dp))
            CaptureButton(
                onClick = {
                    takePhoto(
                        context = context,
                        imageCapture = imageCapture,
                        lensFacing = lensFacing,
                        onImageCaptured = { uri ->
                            onImageCaptured(uri)
                        },
                        onCaptureFailed = {
                            showMessage(context.getString(R.string.capture_failed_message))
                        }
                    )
                },
                isEnable = enableCapture,
            )
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 24.dp)
            )
        }
        Image(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            painter = painterResource(R.drawable.img_home_bottom),
            contentDescription = "Home Bottom Image",
        )
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun CaptureButton(
    onClick: () -> Unit,
    isEnable: Boolean,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth(0.8f),
        onClick = onClick,
        enabled = isEnable,
    ) {
        Text(
            text = stringResource(id = R.string.deepmedi_home_capture_button)
        )
    }
}

@Composable
@Preview
internal fun DeepmediHomeScreenPreview() {
    AppTheme() {
        DeepmediHomeScreen(
            uiState = DeepmediHomeState.Idle,
            onNavigateToResult = {},
            onImageCaptured = {},
            showMessage = {},
        )
    }
}