package com.example.sleep_cycle.mocks.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.modules.Toaster
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import com.example.sleep_cycle.data.repository.SleepTimeRepository
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MockSleepCycleViewModel @Inject constructor(
    sleepCycleRepository: SleepCycleRepository,
    sleepTimeRepository: SleepTimeRepository,
    toaster: Toaster,
    @ApplicationContext appContext: Context
) : SleepCycleViewModel(sleepCycleRepository, sleepTimeRepository, toaster, appContext) {

    private val _testSleepCycles = MutableLiveData<List<SleepCycle>>(emptyList())
    override val sleepCycles: LiveData<List<SleepCycle>> get() = _testSleepCycles

    override fun getAllSleepCycles(): List<SleepCycle> {
        return _testSleepCycles.value.orEmpty()
    }

    fun setTestSleepCycles(cycles: List<SleepCycle>) {
        _testSleepCycles.postValue(cycles)
    }

    override fun addSleepCycle(sleepCycle: SleepCycle) {
        val updatedList = _testSleepCycles.value.orEmpty() + sleepCycle
        _testSleepCycles.postValue(updatedList)
    }
}
