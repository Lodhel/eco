package com.example.dtl.presentation.feature

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.dtl.R
import com.example.dtl.domain.NetworkMonitor
import com.example.dtl.presentation.component.BlueButton
import com.example.dtl.presentation.component.GreenButton
import com.example.dtl.presentation.component.LightGreenButton
import com.example.dtl.presentation.component.PhotoBottomSheet
import com.example.dtl.presentation.component.RoundedAlertDialog
import com.example.dtl.presentation.theme.ButtonBlue
import com.example.dtl.presentation.theme.ButtonLightestBlue
import com.example.dtl.presentation.theme.Green
import com.example.dtl.presentation.theme.Grey
import com.example.dtl.presentation.theme.LighterGreen
import com.example.dtl.presentation.theme.LightestGrey
import com.example.dtl.presentation.viewModel.MainViewModel
import java.io.File

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MakeRequestScreen(modifier: Modifier = Modifier, viewModel: MainViewModel = hiltViewModel(), onMyRequests: () -> Unit) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val networkMonitor = remember { NetworkMonitor(context) }
    val isOnline by networkMonitor.isOnline.collectAsState(initial = false)

    var reportTitle by rememberSaveable { mutableStateOf("") }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showPhotoBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showSuccessAlertDialog by rememberSaveable { mutableStateOf(false) }

    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            imageUri = cameraImageUri
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = uri
        }
    }

    if (showSuccessAlertDialog) {
        RoundedAlertDialog(
            title = "Заявка была отправлена",
            text = "Вы сможете найти ее в разделе \"Все заявки\"",
            onDismiss = { showSuccessAlertDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessAlertDialog = false
                        onMyRequests.invoke()
                    }
                ) {
                    Text("В заявки")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSuccessAlertDialog = false
                    }
                ) {
                    Text("Ок")
                }
            },
        )
    }

    if (showPhotoBottomSheet) {
        PhotoBottomSheet(
            onCameraClick = {
                showPhotoBottomSheet = false
                val file = createImageFile(context)
                cameraImageUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                cameraLauncher.launch(cameraImageUri!!)
            },
            onGalleryClick = {
                showPhotoBottomSheet = false
                galleryLauncher.launch("image/*")
            },
            onDismiss = { showPhotoBottomSheet = false }
        )
    }

    val scroll = rememberScrollState()

    Column(
        modifier
            .background(Color.White)
            .fillMaxSize()
            .verticalScroll(scroll),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Создание заявки",
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
        Spacer(modifier = Modifier.height(16.dp))
        Text("Добавьте 1 фото", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(14.dp))
        if (imageUri != null) {
            Box {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Plant picture",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .fillMaxHeight()
                        .heightIn(max = (configuration.screenHeightDp * 0.5f).dp)
                )
                Image(
                    painter = painterResource(R.drawable.ic_close_circle),
                    contentDescription = "Unpin photo",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clickable {
                            imageUri = null
                        }
                )
            }
        } else {
            Box(modifier = Modifier
                .background(ButtonLightestBlue, RoundedCornerShape(12.dp))
                .height((configuration.screenHeightDp * 0.3f).dp)
                .width((configuration.screenWidthDp * 0.5f).dp)
                .clickable {
                    showPhotoBottomSheet = true
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_gallery_add),
                    contentDescription = null,
                    tint = ButtonBlue,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(12.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        if (imageUri != null) {
            LightGreenButton(
                modifier = Modifier.fillMaxWidth().requiredWidthIn(max = 400.dp),
                text = "Заменить фото",
            ) {
                showPhotoBottomSheet = true
            }
        } else {
            BlueButton(
                modifier = Modifier.fillMaxWidth().requiredWidthIn(max = 400.dp),
                text = "Добавить фото",
            ) {
                showPhotoBottomSheet = true
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        GreyEditText(
            value = reportTitle,
            onValueChange = { reportTitle = it },
            label = {
                Text(text = "Название")
            },
            modifier = Modifier.fillMaxWidth().requiredWidthIn(max = 500.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.weight(1f))
        GreenButton(
            modifier = Modifier.fillMaxWidth().requiredWidthIn(max = 500.dp),
            text = "Отправить",
            enabled = imageUri != null
        ) {
            viewModel.addRequest(imageUri.toString(), if (reportTitle != "") reportTitle else "Заявка_${System.currentTimeMillis()}")
            showSuccessAlertDialog = true
            imageUri = null
            reportTitle = ""
        }
    }
}

fun createImageFile(context: Context): File {
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "IMG_${System.currentTimeMillis()}",
        ".jpg",
        storageDir
    )
}

@Composable
fun GreyEditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)?,
    singleLine: Boolean
) {
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = label,
        singleLine = singleLine,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedContainerColor = LightestGrey,
            unfocusedContainerColor = LightestGrey,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            cursorColor = Green,
            focusedLabelColor = Green,
            unfocusedLabelColor = Color.Gray
        ),
        shape = RoundedCornerShape(12.dp)
    )
}