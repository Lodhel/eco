package com.example.dtl.domain

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

object NetworkDownloadManager {
    fun downloadFile(
        context: Context,
        url: String,
        fileName: String,
        dirType: String = Environment.DIRECTORY_DOWNLOADS
    ) {
        val request = DownloadManager.Request(url.toUri()).apply {
            setTitle("Загрузка файла")
            setDescription(fileName)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            setDestinationInExternalPublicDir(dirType, fileName)

            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }
}