package com.greenie.app.feature.menu.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.greenie.app.feature.menu.MenuRoute

const val menuNavigationRoute = "menu_route"

fun NavController.navigateToMenu(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    this.navigate(menuNavigationRoute, navOptions)
}

fun NavGraphBuilder.menuScreen(
    showMessage: (String) -> Unit,
) {
    composable(
        route = menuNavigationRoute,
    ) { _ ->
        MenuRoute(
            showMessage = showMessage
        )
    }
}