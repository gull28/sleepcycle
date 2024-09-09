// SleepCycleModule.kt
package com.example.sleep_cycle.di

import android.content.Context
import com.example.sleep_cycle.data.repository.SleepCycleRepository
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
    fun provideSleepCycleRepository(
        @ApplicationContext context: Context
    ): SleepCycleRepository {
        return SleepCycleRepository(context)
    }
}
