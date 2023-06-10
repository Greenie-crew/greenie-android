package com.greenie.app.feature.tracking

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.greenie.app.core.designsystem.theme.Colors

@Composable
internal fun TrackingRoute(
    showMessage: (String) -> Unit,
    viewModel: TrackingViewModel = hiltViewModel()
) {

}

@Composable
internal fun TrackingScreen(
    showMessage: (String) -> Unit,
) {
    Column {
        Spacer(modifier = Modifier.height(24.dp))
        Image(
            modifier = Modifier
                .height(184.dp)
                .fillMaxWidth(),
            painter = painterResource(id = R.drawable.img_tracking), 
            contentDescription = "Tracking Image"
        )
        Spacer(modifier = Modifier.height(32.dp))

    }
}

@Composable
private fun TrackingAgreeItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .selectable(
                selected = isChecked,
                onClick = { onCheckedChange(!isChecked) }
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = title,
                style = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = LocalTextStyle.current.copy(
                    fontSize = 12.sp,
                    color = Colors.body
                )
            )
        }

    }
}

//@Composable
//fun CircleCheckbox(selected: Boolean, enabled: Boolean = true, onChecked: () -> Unit) {
//
//    val color = MaterialTheme.colorScheme
//    val imageVector = if (selected) Icons.Filled.CheckCircle else Icons.Outlined.Circle
//    val tint = if (selected) color.primary.copy(alpha = 0.8f) else color.white.copy(alpha = 0.8f)
//    val background = if (selected) color.white else Color.Transparent
//
//    IconButton(onClick = { onChecked() },
//        modifier = Modifier.offset(x = 4.dp, y = 4.dp),
//        enabled = enabled) {
//
//        Icon(imageVector = imageVector, tint = tint,
//            modifier = Modifier.background(background, shape = CircleShape),
//            contentDescription = "checkbox")
//    }
//}
