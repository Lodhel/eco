package com.example.dtl.domain

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dtl.data.database.dao.PendingRequestDao
import com.example.dtl.data.database.model.Request
import com.example.dtl.data.database.model.RequestStatus
import com.example.dtl.data.network.RetrofitBuilder
import com.example.dtl.data.network.model.AnalysisResult
import com.example.dtl.data.network.model.OrderResultData
import com.example.dtl.data.network.model.PlantDetails
import com.example.dtl.data.network.model.Response
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class DataRepository(
    private val pendingRequestDao: PendingRequestDao,
    private val workManager: WorkManager,
    @ApplicationContext private val context: Context
) {
    
    suspend fun addRequest(
        filepath: String,
        title: String,
    ): Long {
        val uri = filepath.toUri()
        val file = uriToFile(uri, context)

        val request = Request(
            filepath = file.absolutePath,
            title = title,
        )
        
        return pendingRequestDao.insert(request)
    }

    suspend fun deleteRequest(request: Request) {
        request.server_id?.let {
            RetrofitBuilder.apiService.deleteOrderById(id = it)
        }
        pendingRequestDao.deleteRequest(request)
    }

    fun getAllRequests(): Flow<List<Request>> {
        return pendingRequestDao.getAllRequests()
    }

    fun getAllRequestsAsc(): Flow<List<Request>> {
        return pendingRequestDao.getAllRequestsAsc()
    }

    suspend fun getOrderById(id: Int): Response<OrderResultData> {
        return RetrofitBuilder.apiService.getOrderById(id = id)
    }

    suspend fun getPlantById(order_id: Int, result_id: Int): Response<PlantDetails> {
        return RetrofitBuilder.apiService.getPlantDetailsById(order_id = order_id, result_id = result_id)
    }

    suspend fun getAnalysisById(order_id: Int): Response<AnalysisResult> {
        return RetrofitBuilder.apiService.getOrderAnalysisById(order_id = order_id)
    }
    
    suspend fun processPendingRequests(onProcessingFinished: () -> Unit) {
        val requests = pendingRequestDao.getPendingRequestsSync()
        
        requests.forEach { request ->
            try {
                pendingRequestDao.updateRequest(request.copy(status = RequestStatus.PROCESSING.name))

                val response = executeNetworkRequest(request)
                
                if (response.success) {
                    val updatedRequest = request.copy(
                        server_id = response.data?.id,
                        status = RequestStatus.COMPLETED.name,
                    )
                    pendingRequestDao.updateRequest(updatedRequest)
                } else {
                    pendingRequestDao.updateStatus(request.id, RequestStatus.FAILED.name)
                }
            } catch (e: Exception) {
                Log.e("DataRepository", e.message.orEmpty())
                pendingRequestDao.updateStatus(request.id, RequestStatus.PENDING.name)
            }
        }

        onProcessingFinished.invoke()
    }
    
    private suspend fun executeNetworkRequest(request: Request): Response<OrderResultData> {
        val uri = getUriFromFilePath(request.filepath, context)
        val file = uriToFile(uri, context)
        val filePart = prepareFilePart(file)
        val response =
            RetrofitBuilder.apiService.definePlant(file = filePart, title = request.title)
        return response
    }
    
    fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val syncWork = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()
        
        workManager.enqueue(syncWork)
    }

    private fun getUriFromFilePath(filePath: String, context: Context): Uri {
        return if (filePath.startsWith("content://")) {
            filePath.toUri()
        } else if (filePath.startsWith("/")) {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                File(filePath)
            )
        } else {
            filePath.toUri()
        }
    }

    private fun createImageFile(context: Context): File {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "IMG_${System.currentTimeMillis()}",
            ".jpg",
            storageDir
        )
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val file = createImageFile(context)
        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        return file
    }

    private fun prepareFilePart(file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }
}