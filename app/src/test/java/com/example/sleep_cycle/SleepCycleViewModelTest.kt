package com.example.sleep_cycle

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.modules.Toaster
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import com.example.sleep_cycle.data.repository.SleepTimeRepository
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@ExperimentalCoroutinesApi
class SleepCycleViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private lateinit var viewModel: SleepCycleViewModel
    private lateinit var mockSleepCycleRepository: SleepCycleRepository
    private lateinit var mockSleepTimeRepository: SleepTimeRepository
    private lateinit var mockToaster: Toaster
    private val mockContext: Context = mock(Context::class.java)

    @Before
    fun setup() {
        mockSleepCycleRepository = mock(SleepCycleRepository::class.java)
        mockSleepTimeRepository = mock(SleepTimeRepository::class.java)
        mockToaster = mock(Toaster::class.java)  // Mock Toaster as well

        // Initialize the ViewModel with mocked dependencies
        viewModel = SleepCycleViewModel(mockSleepCycleRepository, mockSleepTimeRepository, mockToaster, mockContext)
    }

    @Test
    fun testAddSleepCycle() = runTest {
        val sleepCycle = SleepCycle(name = "Test Cycle", isActive = 0)

        viewModel.addSleepCycle(sleepCycle)

        verify(mockSleepCycleRepository).addSleepCycleWithTimes(sleepCycle)
    }
}
