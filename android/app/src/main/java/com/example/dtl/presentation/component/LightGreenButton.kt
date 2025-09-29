package com.example.dtl.presentation.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dtl.presentation.theme.Green
import com.example.dtl.presentation.theme.Grey
import com.example.dtl.presentation.theme.LightestGreen
import com.example.dtl.presentation.theme.LightestGrey

@Composable
fun LightGreenButton(modifier: Modifier = Modifier, text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = LightestGreen,
            contentColor = Green,
            disabledContainerColor = LightestGrey,
            disabledContentColor = Grey
        ),
        enabled = enabled,
        onClick = onClick
    ) { Text(text) }
}

@Preview
@Composable
fun LightGreenButtonPreview() {
    LightGreenButton(text = "Hello", enabled = false) { }
}