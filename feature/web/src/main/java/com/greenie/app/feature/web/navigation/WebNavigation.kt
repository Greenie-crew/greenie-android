package com.greenie.app.feature.web.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.greenie.app.feature.web.WebRoute
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

internal const val webUrlArg = "webUrl"
const val webNavigationRoute = "web_route"

internal class WebArgs(
    val url: String,
) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                url = checkNotNull(
                    URLDecoder.decode(
                        savedStateHandle[webUrlArg],
                        StandardCharsets.UTF_8.toString()
                    )
                )
            )
}

fun NavController.navigateToWeb(url: String, navOptions: NavOptionsBuilder.() -> Unit = {}) {
    val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
    this.navigate("$webNavigationRoute/$encodedUrl", navOptions)
}

fun NavGraphBuilder.webScreen(
    showMessage: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToRecord: () -> Unit,
) {
    composable(
        route = "$webNavigationRoute/{$webUrlArg}",
        arguments = listOf(
            navArgument(webUrlArg) {
                type = NavType.StringType
            }
        )
    ) { _ ->
        WebRoute(
            showMessage = showMessage,
            onNavigateBack = onNavigateBack,
            onNavigateToRecord = onNavigateToRecord,
        )
    }
}