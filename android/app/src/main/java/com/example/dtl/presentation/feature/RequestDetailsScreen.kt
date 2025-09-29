package com.example.dtl.presentation.feature

import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.dtl.R
import com.example.dtl.data.network.model.OrderResultData
import com.example.dtl.domain.NetworkDownloadManager
import com.example.dtl.domain.ResourceState
import com.example.dtl.domain.mappers.RectanglesMapper
import com.example.dtl.presentation.component.GreenButton
import com.example.dtl.presentation.component.ImageWithRectangles
import com.example.dtl.presentation.component.LightBlueButton
import com.example.dtl.presentation.component.LightGreenButton
import com.example.dtl.presentation.theme.Grey
import com.example.dtl.presentation.theme.LightestGrey
import com.example.dtl.presentation.viewModel.MainViewModel

@Composable
fun RequestDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    orderId: Int,
    imagepath: String,
    onBackPressed: () -> Unit,
    onPlantClick: (Int) -> Unit,
    onAnalyticsClick: () -> Unit
) {
    LaunchedEffect(orderId) {
        viewModel.loadRequestDetails(orderId)
    }

    val requestState by viewModel.requestDetails

    when (val state = requestState) {
        is ResourceState.Loading -> {}
        is ResourceState.Success -> {
            RequestDetailsContent(
                modifier = modifier,
                request = state.data,
                imagepath = imagepath,
                onBackPressed = onBackPressed,
                onPlantClick = onPlantClick,
                onAnalyticsClick = onAnalyticsClick
            )
        }

        is ResourceState.Error -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailsContent(
    modifier: Modifier,
    request: OrderResultData,
    imagepath: String,
    onBackPressed: () -> Unit,
    onPlantClick: (Int) -> Unit,
    onAnalyticsClick: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    var selectedId by rememberSaveable { mutableStateOf<Int?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val scroll = rememberScrollState()

    Column(
        modifier
            .background(Color.White)
            .fillMaxSize()
            .verticalScroll(scroll),
        horizontalAlignment = Alignment.CenterHorizontally
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
                text = "Результаты анализа",
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = if (configuration.screenWidthDp > 648) Modifier.requiredWidthIn(max = 600.dp) else Modifier) {
            ImageWithRectangles(
                selectedId = selectedId,
                imageUrl = imagepath,
                rectangles = request.results.map{ RectanglesMapper.mapOrderResultToRectangle(it) },
                onRectangleClick = { selectedId = it },
                modifier = Modifier.fillMaxWidth()
            )
            Image(
                painter = painterResource(R.drawable.ic_download),
                contentDescription = "Unpin photo",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clickable {
                        NetworkDownloadManager.downloadFile(
                            context = context,
                            url = request.image_path,
                            fileName = "${request.title}.jpg",
                            dirType = Environment.DIRECTORY_PICTURES,
                        )
                    }
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Породы растений",
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(14.dp))
        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = if (selectedId != null)
                    "${selectedId!! + 1} ${request.results[selectedId!!].name_plant}"
                else "",
                onValueChange = {},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    unfocusedContainerColor = LightestGrey,
                    focusedContainerColor = LightestGrey,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                request.results.forEach { option ->
                    DropdownMenuItem(
                        text = { Text("${request.results.indexOf(option) + 1} ${option.name_plant}") },
                        onClick = {
                            selectedId = request.results.indexOf(option)
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        LightBlueButton(
            modifier = Modifier.fillMaxWidth().requiredWidthIn(max = 600.dp),
            text = "Подробный отчет по насаждению",
            enabled = selectedId != null
        ) {
            selectedId?.let { onPlantClick.invoke(request.results[it].id) }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Spacer(modifier = Modifier.weight(1f))
        LightGreenButton(
            modifier = Modifier.fillMaxWidth().requiredWidthIn(max = 600.dp),
            text = "Общая аналитика",
        ) {
            onAnalyticsClick.invoke()
        }
        Spacer(modifier = Modifier.height(14.dp))
        GreenButton(
            modifier = Modifier.fillMaxWidth().requiredWidthIn(max = 600.dp),
            text = "Выгрузить перечетную ведомость",
        ) {
            NetworkDownloadManager.downloadFile(
                context = context,
                url = request.statement,
                fileName = "ведомость_${request.title}.xlsx",
                dirType = Environment.DIRECTORY_DOWNLOADS,
            )
        }
    }
}
