package com.greenie.app.feature.web

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.web.WebContent
import com.google.accompanist.web.WebViewNavigator
import com.google.accompanist.web.WebViewState
import com.greenie.app.feature.web.navigation.WebArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val webArgs: WebArgs = WebArgs(savedStateHandle)
    private val url: String = webArgs.url

    val webViewState = WebViewState(
        webContent = WebContent.Url(url)
    )
    val webViewNavigator = WebViewNavigator(viewModelScope)


}