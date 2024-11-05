package com.example.sleep_cycle.data.modules

import android.content.Context
import com.example.sleep_cycle.data.AppDatabase
import com.example.sleep_cycle.data.dao.SleepCycleDao
import com.example.sleep_cycle.data.dao.SleepTimeDao
import com.example.sleep_cycle.data.repository.Preference
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import com.example.sleep_cycle.data.repository.SleepTimeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SleepCycleModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideSleepCycleDao(database: AppDatabase): SleepCycleDao {
        return database.sleepCycleDao()
    }

    @Provides
    @Singleton
    fun provideSleepTimeDao(database: AppDatabase): SleepTimeDao {
        return database.sleepTimeDao()
    }

    @Provides
    @Singleton
    fun provideSleepCycleRepository(sleepCycleDao: SleepCycleDao): SleepCycleRepository {
        return SleepCycleRepository(sleepCycleDao)
    }

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): Preference {
        return Preference(context)
    }

    @Provides
    @Singleton
    fun provideSleepTimeRepository(sleepTimeDao: SleepTimeDao): SleepTimeRepository {
        return SleepTimeRepository(sleepTimeDao)
    }
}
