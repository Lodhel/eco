package com.example.dtl.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dtl.R
import com.example.dtl.presentation.theme.Green
import com.example.dtl.presentation.theme.LightestGreen
import com.example.dtl.presentation.theme.LightestGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoBottomSheet(onCameraClick: () -> Unit, onGalleryClick: () -> Unit, onDismiss: () -> Unit) {
    val state = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = state,
        containerColor = Color.Transparent,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = null,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.White)
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
                    .background(LightestGreen, RoundedCornerShape(12.dp))
                    .padding(vertical = 38.dp)
                    .clickable { onCameraClick.invoke() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_gallery_add),
                    contentDescription = null,
                    modifier = Modifier.height(32.dp),
                    tint = Green,
                )
                Text(
                    text = "Камера",
                    color = Green,
                    fontSize = 16.sp
                )
            }

            Column(
                modifier = Modifier.weight(1f)
                    .background(LightestGrey, RoundedCornerShape(12.dp))
                    .padding(vertical = 38.dp)
                    .clickable { onGalleryClick.invoke() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically)
            ) {
                Image(
                    painter = painterResource(R.drawable.gallery),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.height(32.dp),
                )
                Text(
                    text = "Галерея",
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }
        }
    }
}