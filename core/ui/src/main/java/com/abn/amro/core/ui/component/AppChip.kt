package com.abn.amro.core.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    accentColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (selected) accentColor.copy(alpha = 0.2f) else Color.Transparent
    val borderColor = if (selected) accentColor else contentColor.copy(alpha = 0.8f)
    val textColor = if (selected) contentColor else contentColor.copy(alpha = 0.7f)
    val fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier.height(32.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = fontWeight,
                color = textColor
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun AppChipPreview_Selected() {
    Box(Modifier.padding(16.dp)) {
        AppChip(
            label = "Action",
            selected = true,
            onClick = {},
            accentColor = Color(0xFF009287), // Teal
            contentColor = Color.White
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun AppChipPreview_Unselected() {
    Box(Modifier.padding(16.dp)) {
        AppChip(
            label = "Adventure",
            selected = false,
            onClick = {},
            accentColor = Color(0xFF009287),
            contentColor = Color.White
        )
    }
}