package com.greenie.app.feature.result

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.webkit.JavascriptInterface
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import com.greenie.app.core.designsystem.theme.AppTheme
import com.greenie.app.core.model.RecordAnalyzeData

@Composable
internal fun ResultRoute(
    showMessage: (String) -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val resultUiState by viewModel.resultUiState.collectAsStateWithLifecycle()

    when (resultUiState) {
        ResultUiState.LOADING -> LoadingScreen()
        is ResultUiState.LOADED -> ResultScreen(
            showMessage = showMessage,
            recordAnalyzeData = (resultUiState as ResultUiState.LOADED).analyzeResultData,
        )
    }
}

private data class LoadingContentData(val contentResId: Int, val lottieResId: Int)
private val LoadingContentArray = arrayOf(
    LoadingContentData(
        contentResId = R.string.result_loading_content_music,
        lottieResId = R.raw.lottie_speakers_music,
    ),
    LoadingContentData(
        contentResId = R.string.result_loading_content_washing_machine,
        lottieResId = R.raw.lottie_washing_machine,
    ),
    LoadingContentData(
        contentResId = R.string.result_loading_content_cleaner,
        lottieResId = R.raw.lottie_cleaner,
    ),
    LoadingContentData(
        contentResId = R.string.result_loading_content_jumping,
        lottieResId = R.raw.lottie_jumping_monkey,
    ),
    LoadingContentData(
        contentResId = R.string.result_loading_content_construction,
        lottieResId = R.raw.lottie_construction,
    ),
)

@Composable
internal fun LoadingScreen() {
    val loadingContent = remember { LoadingContentArray.random() }
    val content = stringResource(id = loadingContent.contentResId)
    val lottieComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(loadingContent.lottieResId)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(BiasAlignment(0f, -0.3f)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LottieAnimation(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f),
                composition = lottieComposition,
                iterations = LottieConstants.IterateForever,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = content,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
            )
            Spacer(modifier = Modifier.height(32.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(2.dp),
                color = Color(0xFF1A93FE),
                trackColor = Color(0x4D1A93FE),
                strokeCap = StrokeCap.Square,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.result_loading_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

private const val GREENIE_WEB = "http://greenie-web.vercel.app"
private const val AVERAGE_PARAMETER_KEY = "average"

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun ResultScreen(
    showMessage: (String) -> Unit,
    recordAnalyzeData: RecordAnalyzeData,
) {
    val webViewState = rememberWebViewState(
        Uri.parse(GREENIE_WEB)
        .buildUpon()
            .apply {
                recordAnalyzeData.analyzeScoreMap.forEach {
                    appendQueryParameter(it.key.label, it.value.toString())
                }
                appendQueryParameter(AVERAGE_PARAMETER_KEY, recordAnalyzeData..toString()
            }
        .build()
        .toString()
    )
    val webViewNavigator = rememberWebViewNavigator()
    Log.d("ResultScreen", "webViewState.url: ${webViewState.lastLoadedUrl}")

    BackHandler(
        enabled = webViewNavigator.canGoBack,
    ) {
        webViewNavigator.navigateBack()
    }

    WebView(
        modifier = Modifier.fillMaxSize(),
        state = webViewState,
        navigator = webViewNavigator,
        onCreated = { webView ->
            webView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
            }

            webView.addJavascriptInterface(
                ResultWebAppInterface(
                    showMessage = showMessage,
                    onBackPress = { webViewNavigator.navigateBack() },
                    onNavigateToRecord = { webViewNavigator.loadUrl("http://greenie-web.vercel.app/record") },
                ),
                "Android"
            )
        },
    )
}

class ResultWebAppInterface(
    private val showMessage: (String) -> Unit,
    private val onBackPress: () -> Unit,
    private val onNavigateToRecord: () -> Unit,
) {
    @JavascriptInterface
    fun showToast(toast: String) {
        showMessage(toast)
    }

    @JavascriptInterface
    fun onBackPress() {
        onBackPress()
    }

    @JavascriptInterface
    fun onNavigateToRecord() {
        onNavigateToRecord()
    }
}

@Preview
@Composable
internal fun LoadingScreenPreview() {
    AppTheme {
        LoadingScreen()
    }
}