package com.greenie.app.feature.web

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.web.WebView
import com.google.accompanist.web.WebViewNavigator
import com.google.accompanist.web.WebViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
internal fun WebRoute(
    showMessage: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToRecord: () -> Unit,
    viewModel: WebViewModel = hiltViewModel()
) {
    WebScreen(
        showMessage = showMessage,
        onNavigateBack = onNavigateBack,
        onNavigateToRecord = onNavigateToRecord,
        webViewState = viewModel.webViewState,
        webViewNavigator = viewModel.webViewNavigator,
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun WebScreen(
    showMessage: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToRecord: () -> Unit,
    webViewState: WebViewState,
    webViewNavigator: WebViewNavigator,
) {

//    BackHandler(
//        enabled = webViewNavigator.canGoBack,
//    ) {
//        webViewNavigator.navigateBack()
//    }

    val coroutineScope = rememberCoroutineScope()

    WebView(
        modifier = Modifier.fillMaxSize(),
        state = webViewState,
        navigator = webViewNavigator,
        onCreated = { webView ->
            webView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }

            webView.addJavascriptInterface(
                WebAppInterface(
                    _showMessage = { text ->
                        coroutineScope.launch(Dispatchers.Main) {
                            showMessage(text)
                        }
                    },
                    _onNavigateToBack = {
                        coroutineScope.launch(Dispatchers.Main) {
                            onNavigateBack()
                        }
                    },
                    _onNavigateToRecord = {
                        coroutineScope.launch(Dispatchers.Main) {
                            onNavigateToRecord()
                        }
                    },
                ),
                "Android"
            )
        },
    )
}

class WebAppInterface(
    private val _showMessage: (String) -> Unit,
    private val _onNavigateToBack: () -> Unit,
    private val _onNavigateToRecord: () -> Unit,
) {
    @JavascriptInterface
    fun showToast(toast: String) {
        _showMessage(toast)
    }

    @JavascriptInterface
    fun onBackPress() {
        _onNavigateToBack()
    }

    @JavascriptInterface
    fun onNavigateToRecord() {
        _onNavigateToRecord()
    }
}