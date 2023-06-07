package com.greenie.app.feature.record.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.greenie.app.feature.record.RecordRoute

internal const val recordTypeArg = "recordType"
const val recordNavigationRoute = "record_route"

internal class RecordArgs(
//    val type: RecordType,
) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
//                type = checkNotNull(savedStateHandle[recordTypeArg]),
            )
}

fun NavController.navigateToRecord(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    this.navigate(recordNavigationRoute, navOptions)
}

fun NavGraphBuilder.recordScreen(
    showMessage: (String) -> Unit,
    onStartRecord: () -> Unit,
    onPauseRecord: () -> Unit,
    onSaveRecord: () -> Unit,
    onNavigateToResult: (fileName: String) -> Unit,
) {
    composable(
        route = recordNavigationRoute,
    ) { _ ->
        RecordRoute(
            showMessage = showMessage,
            onStartRecord = onStartRecord,
            onPauseRecord = onPauseRecord,
            onSaveRecord = onSaveRecord,
            onNavigateToResult = onNavigateToResult,
        )
    }
}