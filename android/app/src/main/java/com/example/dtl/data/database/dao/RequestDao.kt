package com.example.dtl.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.dtl.data.database.model.Request
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingRequestDao {
    @Insert
    suspend fun insert(request: Request): Long

    @Query("SELECT * FROM requests ORDER BY created_at DESC")
    fun getAllRequests(): Flow<List<Request>>

    @Query("SELECT * FROM requests ORDER BY created_at ASC")
    fun getAllRequestsAsc(): Flow<List<Request>>

    @Query("SELECT * FROM requests WHERE status = 'PENDING' ORDER BY created_at ASC")
    fun getPendingRequests(): Flow<List<Request>>

    @Query("SELECT filepath FROM requests WHERE id = :id")
    suspend fun getRequestImageById(id: Long): List<String>

    @Query("UPDATE requests SET status = :status, attempts = attempts + 1 WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Update
    suspend fun updateRequest(request: Request)

    @Delete
    suspend fun deleteRequest(request: Request)

    @Query("SELECT * FROM requests WHERE status = 'PENDING' OR status = 'PROCESSING'")
    suspend fun getPendingRequestsSync(): List<Request>
}