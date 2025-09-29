package com.example.dtl.presentation.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dtl.presentation.theme.ButtonBlue

@Composable
fun BlueButton(modifier: Modifier = Modifier, text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonBlue,
            contentColor = Color.White,
            disabledContainerColor = ButtonBlue.copy(0.6f),
            disabledContentColor = Color.White.copy(0.6f)
        ),
        enabled = enabled,
        onClick = onClick
    ) { Text(text) }
}

@Preview
@Composable
fun BlueButtonPreview() {
    BlueButton(text = "Hello", enabled = false) { }
}