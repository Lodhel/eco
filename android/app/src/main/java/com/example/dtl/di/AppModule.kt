package com.example.dtl.di

import android.content.Context
import androidx.work.WorkManager
import com.example.dtl.data.database.AppDatabase
import com.example.dtl.data.database.dao.PendingRequestDao
import com.example.dtl.domain.DataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }
    
    @Provides
    fun providePendingRequestDao(database: AppDatabase): PendingRequestDao {
        return database.pendingRequestDao()
    }
    
    @Provides
    @Singleton
    fun provideRepository(
        dao: PendingRequestDao,
        workManager: WorkManager,
        @ApplicationContext context: Context
    ): DataRepository {
        return DataRepository(dao, workManager, context)
    }
}