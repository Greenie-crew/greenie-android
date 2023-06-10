package com.greenie.app.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.greenie.app.common.GREENIE_WEB_URL
import com.greenie.app.core.designsystem.theme.AppTheme
import com.greenie.app.core.designsystem.theme.Colors

const val BANNER_URL = "https://youtu.be/alI91xtwELU"
const val TIP_URL = "$GREENIE_WEB_URL/solution"
const val COUNSEL_URL = "$GREENIE_WEB_URL/mental_hearing_health"
const val HEALTH_URL = "$GREENIE_WEB_URL/health"
const val SHOP_URL = "$GREENIE_WEB_URL/product"

@Composable
internal fun HomeRoute(
    showMessage: (String) -> Unit,
    onNavigateToRecord: () -> Unit,
    onNavigateToTracking: () -> Unit,
    onNavigateToWeb: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uriHandler = LocalUriHandler.current

    HomeScreen(
        showMessage = showMessage,
        onNavigateToRecord = onNavigateToRecord,
        onNavigateToTracking = onNavigateToTracking,
        onNavigateToTip = {
            onNavigateToWeb(TIP_URL)
        },
        onNavigateToCounsel = {
            onNavigateToWeb(COUNSEL_URL)
        },
        onNavigateToHealth = {
            onNavigateToWeb(HEALTH_URL)
        },
        onNavigateToShop = {
            onNavigateToWeb(SHOP_URL)
        },
        onBannerClick = {
            uriHandler.openUri(BANNER_URL)
        }
    )
}

@Composable
internal fun HomeScreen(
    showMessage: (String) -> Unit,
    onNavigateToRecord: () -> Unit,
    onNavigateToTracking: () -> Unit,
    onNavigateToTip: () -> Unit,
    onNavigateToCounsel: () -> Unit,
    onNavigateToHealth: () -> Unit,
    onNavigateToShop: () -> Unit,
    onBannerClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    color = Colors.main_colour
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                modifier = Modifier
                    .padding(vertical = 8.dp),
                painter = painterResource(id = R.drawable.img_home_toolbar),
                contentScale = ContentScale.FillHeight,
                contentDescription = "Home Banner"
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clickable {
                        onBannerClick()
                    }
                    .background(
                        color = Color(0xFFFCDDEC)
                    ),
                painter = painterResource(id = R.drawable.img_home_banner),
                contentScale = ContentScale.FillHeight,
                contentDescription = "Campaign Banner"
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                Row {
                    HomeCardBigItem(
                        modifier = Modifier
                            .weight(1f)
                            .height(234.dp)
                            .background(
                                Color(0xFF2A60B2)
                            ),
                        textColor = Color.White,
                        HomeCardItem(
                            title = stringResource(id = R.string.home_record_button_title),
                            description = stringResource(id = R.string.home_record_button_description),
                            bottomPainter = painterResource(id = R.drawable.ic_home_mic),
                            onClick = onNavigateToRecord
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    HomeCardBigItem(
                        modifier = Modifier
                            .weight(1f)
                            .height(234.dp)
                            .background(
                                Color(0xFF82DFFC)
                            ),
                        textColor = Colors.headline,
                        HomeCardItem(
                            title = stringResource(id = R.string.home_tracking_button_title),
                            description = stringResource(id = R.string.home_tracking_button_description),
                            bottomPainter = painterResource(id = R.drawable.ic_home_tracking),
                            onClick = onNavigateToTracking
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    HomeCardMiddleItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(Colors.bg_light),
                        HomeCardItem(
                            title = stringResource(id = R.string.home_tip_button_title),
                            description = stringResource(id = R.string.home_tip_button_description),
                            bottomPainter = painterResource(id = R.drawable.ic_home_house),
                            onClick = onNavigateToTip
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HomeCardMiddleItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(Colors.bg_light),
                        HomeCardItem(
                            title = stringResource(id = R.string.home_counsel_button_title),
                            description = stringResource(id = R.string.home_counsel_button_description),
                            bottomPainter = painterResource(id = R.drawable.ic_home_counsel),
                            onClick = onNavigateToCounsel
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    HomeCardSmallItem(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp)
                            .background(Colors.bg_light),
                        HomeCardItem(
                            title = stringResource(id = R.string.home_health_button_title),
                            description = stringResource(id = R.string.home_health_button_title),
                            bottomPainter = painterResource(id = R.drawable.ic_home_heart),
                            onClick = onNavigateToHealth
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    HomeCardSmallItem(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp)
                            .background(Colors.bg_light),
                        HomeCardItem(
                            title = stringResource(id = R.string.home_shop_button_title),
                            description = stringResource(id = R.string.home_shop_button_description),
                            bottomPainter = painterResource(id = R.drawable.ic_home_shop),
                            onClick = onNavigateToShop
                        )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

private data class HomeCardItem(
    val title: String,
    val description: String,
    val bottomPainter: Painter,
    val onClick: () -> Unit,
)

@Composable
private fun HomeCardBigItem(
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    cardItem: HomeCardItem,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                cardItem.onClick()
            }
            .then(modifier),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = cardItem.title,
                style = LocalTextStyle.current.copy(
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = cardItem.description,
                style = LocalTextStyle.current.copy(
                    color = textColor,
                    fontSize = 12.sp
                )
            )
        }
        Image(
            modifier = Modifier
                .padding(bottom = 16.dp, end = 12.dp)
                .size(80.dp)
                .align(Alignment.BottomEnd),
            painter = cardItem.bottomPainter,
            contentDescription = "Item Bottom Image"
        )
    }
}

@Composable
private fun HomeCardMiddleItem(
    modifier: Modifier = Modifier,
    cardItem: HomeCardItem,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                cardItem.onClick()
            }
            .then(modifier)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 10.dp)
        ) {
            Text(
                text = cardItem.title,
                style = LocalTextStyle.current.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = cardItem.description,
                style = LocalTextStyle.current.copy(
                    fontSize = 12.sp
                )
            )
        }
        Image(
            modifier = Modifier
                .size(60.dp),
            painter = cardItem.bottomPainter,
            contentDescription = "Item Bottom Image"
        )
    }
}

@Composable
private fun HomeCardSmallItem(
    modifier: Modifier = Modifier,
    cardItem: HomeCardItem,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                cardItem.onClick()
            }
            .then(modifier),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = cardItem.title,
                style = LocalTextStyle.current.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = cardItem.description,
                style = LocalTextStyle.current.copy(
                    fontSize = 12.sp
                )
            )
        }
        Image(
            modifier = Modifier
                .padding(bottom = 12.dp, end = 16.dp)
                .size(60.dp)
                .align(Alignment.BottomEnd),
            painter = cardItem.bottomPainter,
            contentDescription = "Item Bottom Image"
        )
    }
}

@Preview(
    widthDp = 360,
    heightDp = 640,
    showBackground = true,
)
@Composable
internal fun HomeRoutePreview() {
    AppTheme {
        HomeScreen(
            showMessage = {},
            onNavigateToRecord = {},
            onNavigateToTracking = {},
            onNavigateToTip = {},
            onNavigateToCounsel = {},
            onNavigateToHealth = {},
            onNavigateToShop = {},
            onBannerClick = {}
        )
    }
}