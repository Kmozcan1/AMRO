package com.abn.amro.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abn.amro.core.ui.theme.AmroTeal

@Composable
fun LoadingView(modifier: Modifier = Modifier, tint: Color = AmroTeal) {
    Box(modifier.fillMaxSize(), Alignment.Center) {
        CircularProgressIndicator(color = tint)
    }
}

@Composable
fun ErrorView(
    message: String,
    buttonText: String,
    icon: ImageVector,
    onRetry: () -> Unit = { },
    modifier: Modifier = Modifier,
    hasButton: Boolean = true,
    tint: Color = AmroTeal
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = tint
        )

        Spacer(Modifier.height(16.dp))

        Text(text = message, style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(24.dp))

        if (hasButton) {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = tint,
                )
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 200)
@Composable
private fun LoadingViewPreview() {
    LoadingView(tint = Color.Cyan)
}

@Preview(showBackground = true, heightDp = 400)
@Composable
private fun ErrorViewPreview() {
    ErrorView(
        message = "Something went wrong. Please check your internet connection.",
        buttonText = "Retry",
        icon = Icons.Default.Warning,
        onRetry = {}
    )
}