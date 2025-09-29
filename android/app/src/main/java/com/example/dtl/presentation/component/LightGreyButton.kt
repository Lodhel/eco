package com.example.dtl.presentation.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dtl.presentation.theme.DarkerGrey
import com.example.dtl.presentation.theme.LightestGrey

@Composable
fun LightGreyButton(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = LightestGrey,
            contentColor = DarkerGrey
        ),
        onClick = onClick
    ) { Text(text) }
}

@Preview
@Composable
fun LightGreyButtonPreview() {
    LightGreyButton(text = "Hello") { }
}