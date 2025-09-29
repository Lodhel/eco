package com.example.dtl.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dtl.data.database.dao.PendingRequestDao
import com.example.dtl.data.database.model.Request

@Database(entities = [Request::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dtl_app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

    abstract fun pendingRequestDao(): PendingRequestDao
}