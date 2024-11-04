package com.example.sleep_cycle.dao

import com.example.sleep_cycle.data.dao.SleepCycleDao
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.models.SleepTime

class MockSleepCycleDao : SleepCycleDao {
    private val sleepCycles = mutableListOf<SleepCycle>()

    override suspend fun addSleepCycle(sleepCycle: SleepCycle): Long {
        sleepCycles.add(sleepCycle)

        return sleepCycle.id
    }

    override suspend fun insertSleepTimes(sleepTimes: List<SleepTime>) {
        val sleepCycle = sleepCycles.find { it.id == sleepTimes[0].scheduleId }

        if (sleepCycle != null) {
            sleepCycle.sleepTimes = sleepTimes
        }
    }

    override suspend fun toggleActive(id: Long, isActive: Int) {
        val currentlyActiveCycle = sleepCycles.find { it.isActive == 1 }

        if(currentlyActiveCycle != null && currentlyActiveCycle.id != id) {
            currentlyActiveCycle.isActive = 0
        }

        val activeCycle = sleepCycles.find { it.id == id }

        if (activeCycle != null) {
            activeCycle.isActive = 1
        }
    }

    override suspend fun deleteSleepTimesByScheduleId(scheduleId: Long) {
        sleepCycles.removeIf { it.id == scheduleId }
    }

    override suspend fun unsetAllActiveCycles() {
        sleepCycles.map { it.isActive = 0 }
    }

    override suspend fun getSleepCycleById(id: Long): SleepCycle? {
        return sleepCycles.find { it.id == id }
    }

    override suspend fun getSleepTimesForCycle(scheduleId: Long): List<SleepTime> {
        return getSleepCycleById(scheduleId)?.sleepTimes ?: listOf()
    }

    override suspend fun getAllSleepCyclesRaw(): List<SleepCycle> {
        return sleepCycles
    }

    override suspend fun updateSleepCycle(sleepCycle: SleepCycle): Int {
        val index = sleepCycles.indexOfFirst { it.id == sleepCycle.id }
        return if (index != -1) {
            sleepCycles[index] = sleepCycle
            1
        } else {
            0
        }
    }

    override suspend fun deleteSleepCycle(sleepCycle: SleepCycle) {
        sleepCycles.remove(sleepCycle)
    }

    override suspend fun deleteSleepCycleById(id: Long) {
        val itemToRemove = sleepCycles.find { it.id == id }
        if (itemToRemove != null) {
            sleepCycles.remove(itemToRemove)
        }
    }
}
