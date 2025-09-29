package com.example.dtl.presentation.feature

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.dtl.R
import com.example.dtl.data.network.model.PlantDetails
import com.example.dtl.domain.ResourceState
import com.example.dtl.presentation.theme.DarkerGrey
import com.example.dtl.presentation.theme.Grey
import com.example.dtl.presentation.theme.LightestGrey
import com.example.dtl.presentation.viewModel.MainViewModel

@Composable
fun PlantDetailsScreen(modifier: Modifier = Modifier, viewModel: MainViewModel = hiltViewModel(), orderId: Int, resultId: Int, onBackPressed: () -> Unit) {

    LaunchedEffect(orderId, resultId) {
        viewModel.loadPlantDetails(orderId, resultId)
    }

    val plantState by viewModel.plantDetails

    when (val state = plantState) {
        is ResourceState.Loading -> {}
        is ResourceState.Success -> {
            PlantDetailsContent(modifier = modifier, plant = state.data, onBackPressed = onBackPressed)
        }
        is ResourceState.Error -> {}
    }
}

@Composable
fun PlantDetailsContent(modifier: Modifier, plant: PlantDetails, onBackPressed: () -> Unit) {
    val scroll = rememberScrollState()

    Column(
        modifier
            .background(Color.White)
            .fillMaxSize()
            .widthIn(max = 700.dp)
            .verticalScroll(scroll),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(modifier = Modifier.align(Alignment.CenterStart), onClick = { onBackPressed.invoke() }) {
                Icon(
                    painterResource(R.drawable.ic_chevron_left),
                    contentDescription = "Back",
                    tint = Grey
                )
            }
            Text(
                text = "Подробный отчет",
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Вид",
            fontSize = 18.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = plant.name,
            fontSize = 16.sp,
            color = DarkerGrey,
            modifier = Modifier.background(color = LightestGrey, RoundedCornerShape(12.dp))
                .padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Семейство",
            fontSize = 18.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = plant.family,
            fontSize = 16.sp,
            color = DarkerGrey,
            modifier = Modifier.background(color = LightestGrey, RoundedCornerShape(12.dp))
                .padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Род",
            fontSize = 18.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = plant.genus,
            fontSize = 16.sp,
            color = DarkerGrey,
            modifier = Modifier.background(color = LightestGrey, RoundedCornerShape(12.dp))
                .padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Где растет",
            fontSize = 18.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = plant.growing_area,
            fontSize = 16.sp,
            color = DarkerGrey,
            modifier = Modifier.background(color = LightestGrey, RoundedCornerShape(12.dp))
                .padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth()
        )
        plant.height?.let {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Высота",
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = plant.height,
                fontSize = 16.sp,
                color = DarkerGrey,
                modifier = Modifier.background(color = LightestGrey, RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth()
            )
        }
        plant.class_type?.let {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Класс",
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = plant.class_type,
                fontSize = 16.sp,
                color = DarkerGrey,
                modifier = Modifier.background(color = LightestGrey, RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Плоды",
            fontSize = 18.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = if (plant.has_fruits) "Есть" else "Нет",
            fontSize = 16.sp,
            color = DarkerGrey,
            modifier = Modifier.background(color = LightestGrey, RoundedCornerShape(12.dp))
                .padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth()
        )
        plant.dry_branches_percentage?.let {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Сухих ветвей",
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${plant.dry_branches_percentage.toInt()}%",
                fontSize = 16.sp,
                color = DarkerGrey,
                modifier = Modifier.background(color = LightestGrey, RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Состояние",
            fontSize = 18.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = when(plant.status) {
                1 -> "Хорошее"
                3 -> "Плохое"
                else -> "Удовлетворительное"
            },
            fontSize = 16.sp,
            color = DarkerGrey,
            modifier = Modifier.background(color = LightestGrey, RoundedCornerShape(12.dp))
                .padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth()
        )
    }
}
