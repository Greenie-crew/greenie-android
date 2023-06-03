package com.greenie.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import com.greenie.app.feature.menu.navigation.menuNavigationRoute
import com.greenie.app.feature.record.navigation.recordNavigationRoute
import com.greenie.app.feature.history.navigation.historyNavigationRoute
import com.greenie.app.feature.menu.R as MenuR
import com.greenie.app.feature.record.R as RecordR
import com.greenie.app.feature.history.R as HistoryR

val BottomNavigationItems = listOf(
    TopLevelDestination.Menu,
    TopLevelDestination.History,
)

enum class TopLevelDestination(
    val route: String,
    val titleId: Int? = null,
    val labelId: Int? = null,
) {
    Menu(
        menuNavigationRoute,
        null,
        MenuR.string.menu_navigation_label,
    ),
    Record(
        recordNavigationRoute,
        RecordR.string.record_title,
        null,
    ),
    History(
        historyNavigationRoute,
        HistoryR.string.history_title,
        HistoryR.string.history_navigation_label,
    )
}

@Composable
internal fun getDestination(navBackStackEntry: NavBackStackEntry?): TopLevelDestination? {
    val route = navBackStackEntry?.destination?.route ?: return null
    return TopLevelDestination.values().find { it.route == route }
}