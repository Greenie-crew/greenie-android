package com.greenie.app.feature.web

import android.annotation.SuppressLint
import android.content.Intent
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.web.AccompanistWebViewClient
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
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    WebView(
        modifier = Modifier.fillMaxSize(),
        state = webViewState,
        navigator = webViewNavigator,
        client = object : AccompanistWebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                if (request.url.scheme == "intent") {
                    val intent = Intent.parseUri(request.url.toString(), Intent.URI_INTENT_SCHEME)
                    context.startActivity(intent)
                    return true
                } else if (request.url.scheme == "tel") {
                    val intent = Intent(Intent.ACTION_DIAL, request.url)
                    context.startActivity(intent)
                    return true
                }

                // 나머지 서비스 로직 구현
                return false
            }
        },
        onCreated = { webView ->
            webView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                setSupportMultipleWindows(true)
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
                    _copyToClipboard = { text ->
                        clipboardManager.setText(AnnotatedString(text))
                    },
                ),
                "Android"
            )
        }
    )
}


class WebAppInterface(
    private val _showMessage: (String) -> Unit,
    private val _onNavigateToBack: () -> Unit,
    private val _onNavigateToRecord: () -> Unit,
    private val _copyToClipboard: (String) -> Unit,
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

    @JavascriptInterface
    fun copyToClipboard(text: String) {
        _copyToClipboard(text)
    }
}