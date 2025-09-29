package com.example.dtl.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RoundedAlertDialog(
    title: String = "",
    text: String = "",
    onDismiss: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: (@Composable () -> Unit)?,
    ) {
    AlertDialog(
        modifier = Modifier.background(Color.White, RoundedCornerShape(20.dp)),
        containerColor = Color.Transparent,
        onDismissRequest = onDismiss,
        title = {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        },
        text = {
            Text(text, fontSize = 14.sp)
        },
        confirmButton = confirmButton,
        dismissButton = dismissButton
    )
}