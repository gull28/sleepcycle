// TestModule.kt
package com.example.sleep_cycle

import android.content.Context
import com.example.sleep_cycle.mocks.dao.MockSleepCycleDao
import com.example.sleep_cycle.mocks.dao.MockSleepTimeDao
import com.example.sleep_cycle.data.modules.SleepCycleModule
import com.example.sleep_cycle.mocks.repository.FakeSleepCycleRepository
import com.example.sleep_cycle.mocks.repository.FakeSleepTimeRepository
import com.example.sleep_cycle.data.modules.Toaster
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import com.example.sleep_cycle.data.repository.SleepTimeRepository
import com.example.sleep_cycle.mocks.modules.FakeToaster
import com.example.sleep_cycle.mocks.viewmodel.MockSleepCycleViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SleepCycleModule::class]
)
object TestModule {

    @Singleton
    @Provides
    fun provideMockSleepCycleDao(): MockSleepCycleDao = MockSleepCycleDao()

    @Singleton
    @Provides
    fun provideMockSleepTimeDao(): MockSleepTimeDao = MockSleepTimeDao()

    @Singleton
    @Provides
    fun provideFakeSleepCycleRepository(
        mockSleepCycleDao: MockSleepCycleDao
    ): SleepCycleRepository = FakeSleepCycleRepository(mockSleepCycleDao)

    @Singleton
    @Provides
    fun provideFakeSleepTimeRepository(
        mockSleepTimeDao: MockSleepTimeDao
    ): SleepTimeRepository = FakeSleepTimeRepository(mockSleepTimeDao)

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context = context

    // Remove or comment out the Toaster provider here
    // @Singleton
    // @Provides
    // fun provideToaster(): Toaster = FakeToaster

    @Singleton
    @Provides
    fun provideMockSleepCycleViewModel(
        sleepCycleRepository: SleepCycleRepository,
        sleepTimeRepository: SleepTimeRepository,
        toaster: Toaster,
        @ApplicationContext context: Context
    ): MockSleepCycleViewModel = MockSleepCycleViewModel(
        sleepCycleRepository,
        sleepTimeRepository,
        toaster,
        context
    )
}
