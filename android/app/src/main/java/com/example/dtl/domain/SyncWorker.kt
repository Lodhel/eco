package com.example.dtl.domain

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.dtl.data.database.AppDatabase

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val repository = provideRepository(applicationContext)
            repository.processPendingRequests {}
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun provideRepository(context: Context): DataRepository {
        val database = AppDatabase.getInstance(context)
        val workManager = WorkManager.getInstance(context)
        return DataRepository(database.pendingRequestDao(), workManager, context)
    }
}