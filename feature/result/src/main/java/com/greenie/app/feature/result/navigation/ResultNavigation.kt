package com.greenie.app.feature.result.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.greenie.app.feature.result.ResultRoute

internal const val resultRecordFileNameArg = "resultRecordFileName"
const val resultNavigationRoute = "result_route"

internal class ResultArgs(
    val fileName: String,
) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(fileName = checkNotNull(savedStateHandle[resultRecordFileNameArg]))
}

fun NavController.navigateToResult(fileName: String, navOptions: NavOptionsBuilder.() -> Unit = {}) {
    this.navigate("$resultNavigationRoute/$fileName", navOptions)
}

fun NavGraphBuilder.resultScreen(
    showMessage: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToWeb: (String) -> Unit,
) {
    composable(
        route = "$resultNavigationRoute/{$resultRecordFileNameArg}",
        arguments = listOf(
            navArgument(resultRecordFileNameArg) {
                type = NavType.StringType
            }
        )
    ) { _ ->
        ResultRoute(
            showMessage = showMessage,
            onNavigateBack = onNavigateBack,
            onNavigateToWeb = onNavigateToWeb,
        )
    }
}