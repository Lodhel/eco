package com.example.dtl.presentation.feature

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.dtl.R
import com.example.dtl.data.database.model.Request
import com.example.dtl.data.database.model.RequestStatus
import com.example.dtl.domain.NetworkMonitor
import com.example.dtl.presentation.component.RoundedAlertDialog
import com.example.dtl.presentation.theme.Blue
import com.example.dtl.presentation.theme.DarkGrey
import com.example.dtl.presentation.theme.Green
import com.example.dtl.presentation.theme.Grey
import com.example.dtl.presentation.theme.LighterGreen
import com.example.dtl.presentation.theme.LightestGreen
import com.example.dtl.presentation.theme.LightestGrey
import com.example.dtl.presentation.theme.Orange
import com.example.dtl.presentation.theme.StateRed
import com.example.dtl.presentation.viewModel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRequestsScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    onRequestClick: (Int, String) -> Unit
) {
    val allRequests by viewModel.allRequests.collectAsState()

    val context = LocalContext.current
    val networkMonitor = remember { NetworkMonitor(context) }
    val isOnline by networkMonitor.isOnline.collectAsState(initial = false)
    var isAscending by rememberSaveable { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    var showDeleteAlertDialog by rememberSaveable { mutableStateOf<Request?>(null) }
    val state = rememberPullToRefreshState()
    
    if (showDeleteAlertDialog != null) {
        RoundedAlertDialog(
            title = "Удалить заявку?",
            text = "Отменить это действие будет невозможно",
            onDismiss = { showDeleteAlertDialog = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRequest(showDeleteAlertDialog!!)
                        showDeleteAlertDialog = null
                    }
                ) {
                    Text("Да")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteAlertDialog = null
                    }
                ) {
                    Text("Нет")
                }
            }
        )
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            viewModel.manualSync { isRefreshing = false }
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
        },
        state = state,
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                containerColor = LightestGreen,
                color = Green,
                state = state
            )
        },
    ) {
        Column(
            modifier
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Все заявки",
                    textAlign = TextAlign.Start,
                    fontSize = 24.sp
                )
                Row(
                    modifier = Modifier
                        .background(if (isOnline) LighterGreen else Grey, RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_dot),
                        tint = Color.White,
                        contentDescription = null,
                        modifier = Modifier.size(10.dp),
                    )
                    Text(
                        if (isOnline) "Online" else "Offline",
                        textAlign = TextAlign.Start,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .background(LightestGrey, RoundedCornerShape(20.dp))
                    .padding(8.dp)
                    .clickable {
                        isAscending = !isAscending
                        viewModel.changeAscendingSort(isAscending)
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(R.drawable.ic_sort),
                    contentDescription = "Sort",
                    tint = DarkGrey
                )
                Text(
                    text = if (isAscending) "Сначала старые" else "Сначала новые",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGrey
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Adaptive(300.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(allRequests) { _, item ->
                    RequestCard(
                        request = item,
                        onDelete = { showDeleteAlertDialog = item },
                        onCardClick = {
                            if (isOnline) {
                                item.server_id?.let { onRequestClick(it, item.filepath) }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RequestCard(request: Request, onDelete: () -> Unit, onCardClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(LightestGrey, RoundedCornerShape(14.dp))
            .padding(14.dp)
            .clickable { onCardClick.invoke() },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val backgroundColor = when (request.status) {
                RequestStatus.COMPLETED.name -> Green
                RequestStatus.PENDING.name -> Orange
                RequestStatus.PROCESSING.name -> Blue
                else -> Color.Gray
            }
            Text(
                text = when (request.status) {
                    RequestStatus.COMPLETED.name -> "Выполнено"
                    RequestStatus.PENDING.name -> "На рассмотрении"
                    RequestStatus.PROCESSING.name -> "Выполняется"
                    else -> "Не удалось выполнить"
                },
                fontSize = 14.sp,
                modifier = Modifier
                    .background(backgroundColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp),
                color = Color.White
            )
            IconButton(onClick = { onDelete.invoke() }) {
                Icon(
                    painterResource(R.drawable.ic_delete),
                    contentDescription = "Back",
                    tint = StateRed
                )
            }
        }
        Spacer(Modifier.height(4.dp))

        Text(request.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)

        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val dateString = sdf.format(Date(request.created_at))
        Text(dateString, fontSize = 12.sp)
        Spacer(Modifier.height(4.dp))

        AsyncImage(
            model = request.filepath.toUri(),
            contentDescription = "Plant picture",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .heightIn(max = 168.dp)
        )
    }
}