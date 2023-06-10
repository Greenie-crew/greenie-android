package com.greenie.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import com.greenie.app.feature.home.navigation.homeNavigationRoute
import com.greenie.app.feature.record.navigation.recordNavigationRoute
import com.greenie.app.feature.history.navigation.historyNavigationRoute
import com.greenie.app.feature.home.R as MenuR
import com.greenie.app.feature.record.R as RecordR
import com.greenie.app.feature.history.R as HistoryR
import com.greenie.app.feature.tracking.R as TrackingR

val BottomNavigationItems = listOf(
    TopLevelDestination.Home,
    TopLevelDestination.History,
)


/**
 * If you want to add top app bar, you can add it here.
 */
enum class TopLevelDestination(
    val route: String,
    val titleId: Int? = null,
    val labelId: Int? = null,
) {
    Home(
        homeNavigationRoute,
        null,
        MenuR.string.home_navigation_label,
    ),
    Record(
        recordNavigationRoute,
        RecordR.string.record_title,
        null,
    ),
    Tracking(
        "tracking",
        TrackingR.string.tracking_title,
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