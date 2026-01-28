package com.abn.amro.core.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppBadge(
    text: String,
    contentColor: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    useBorder: Boolean = false
) {
    Surface(
        color = contentColor.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small,
        border = if (useBorder) {
            BorderStroke(width = 1.dp, color = contentColor.copy(alpha = 0.2f))
        } else {
            null
        },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = contentColor
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppBadgePreview_Simple() {
    Box(Modifier.padding(16.dp)) {
        AppBadge(
            text = "Action",
            contentColor = Color.Blue,
            useBorder = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppBadgePreview_WithIconAndBorder() {
    Box(Modifier.padding(16.dp)) {
        AppBadge(
            text = "8.5",
            contentColor = Color.Yellow,
            icon = Icons.Default.Star,
            useBorder = true
        )
    }
}