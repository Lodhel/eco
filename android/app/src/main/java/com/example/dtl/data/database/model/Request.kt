package com.example.dtl.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "requests")
data class Request(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val filepath: String,
    val title: String,
    val created_at: Long = System.currentTimeMillis(),
    var attempts: Int = 0,
    var status: String = RequestStatus.PENDING.name,
    var server_id: Int? = null,
)