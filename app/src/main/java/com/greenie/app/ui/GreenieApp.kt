package com.greenie.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.greenie.app.core.designsystem.icon.AppIcons
import com.greenie.app.core.designsystem.theme.Colors
import com.greenie.app.feature.record.navigation.navigateToRecord
import com.greenie.app.navigation.BottomNavigationItems
import com.greenie.app.navigation.GreenieNavHost
import com.greenie.app.navigation.TopLevelDestination
import com.greenie.app.navigation.getDestination
import com.greenie.app.core.designsystem.R as DesignSystemR

const val BottomBarHeight = 52

@Composable
fun GreenieApp() {
    val navHostController = rememberNavController()
    val backStackEntry by navHostController.currentBackStackEntryAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = BottomBarHeight.dp)
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White,
                                Colors.bg_light,
                            )
                        ),
                        alpha = 0.4f
                    )
            ) {
                val titleId = getDestination(navBackStackEntry = backStackEntry)?.titleId
                if (titleId != null) {
                    val title = stringResource(id = titleId)
                    TopAppBar(
                        title = title,
                        onBackClick =
                        if (navHostController.previousBackStackEntry != null) {
                            { navHostController.popBackStack() }
                        } else {
                            null
                        },
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = Colors.line_light,
                        thickness = 1.dp,
                    )
                }

                GreenieNavHost(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    navController = navHostController,
                    snackbarHostState = snackbarHostState,
                )
            }

            AppBottomBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BottomBarHeight.dp)
                    .align(Alignment.BottomCenter),
                destination = BottomNavigationItems,
                onNavigateToDestination = { destination ->
                    navHostController.navigate(destination.route) {
                        popUpTo(navHostController.graph.startDestinationId) {
                            saveState = true
                            inclusive = false
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                onClickFab = {
                    navHostController.navigateToRecord() {
                        popUpTo(navHostController.graph.startDestinationId) {
                            saveState = true
                            inclusive = false
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                currentDestination = backStackEntry?.destination
            )
        }
    }
}

@Composable
private fun TopAppBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        if (onBackClick != null) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(56.dp),
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = AppIcons.ArrowBack,
                    contentDescription = "Back",
                    tint = Colors.headline,
                )
            }
        }
        Text(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Center),
            text = title,
            fontSize = 20.sp,
            color = Colors.headline,
            maxLines = 1,
        )
    }
}

@Composable
private fun BoxScope.AppBottomBar(
    modifier: Modifier = Modifier,
    destination: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    onClickFab: () -> Unit,
    currentDestination: NavDestination?,
) {
    AppNavigationBar(
        modifier = modifier,
    ) {
        destination.forEach { destination ->
            val isSelected = currentDestination?.route == destination.route
            AppNavigationBarItem(
                selected = isSelected,
                onClick = { onNavigateToDestination(destination) },
                labelId = destination.labelId,
            )
        }
    }

    Image(
        modifier = Modifier
            .wrapContentSize()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
            .height(64.dp)
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
            .clip(CircleShape)
            .clickable {
                onClickFab()
            }
            .align(Alignment.BottomCenter),
        painter = painterResource(id = DesignSystemR.drawable.ic_navigation_menu),
        contentDescription = "Menu"
    )
}

@Composable
private fun AppNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = content,
    )
}

@Composable
private fun RowScope.AppNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    labelId: Int?,
) {
    Column(
        modifier = Modifier
            .selectable(
                selected = selected,
                onClick = onClick,
            )
            .weight(1f)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier
                .wrapContentSize(),
            text = if (labelId != null) {
                stringResource(labelId)
            } else {
                ""
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = if (selected) {
                Color.Black
            } else {
                Colors.body
            },
            maxLines = 1,
        )
    }
}